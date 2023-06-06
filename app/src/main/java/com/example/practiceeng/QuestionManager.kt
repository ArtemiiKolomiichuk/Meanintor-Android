package com.example.practiceeng

import com.example.practiceeng.database.WordRepository
import kotlinx.coroutines.flow.toList
import java.lang.Exception
import java.util.Date
import java.util.Random
import java.util.UUID

/**
 * Manages the questions
 * @see reset
 * @see getQuestions
 * @see submitAnswer
 */
class QuestionManager
{
    companion object {
        private var folders: Array<UUID> = arrayOf()
        private var testTypes: Array<TestType> = arrayOf()
        private var cards: MutableList<WordCard> = mutableListOf()
        private var counter: Int = 0

        /**
         * Resets the question manager
         * @param testTypes the types of tests that can be selected
         * @param folders folders from which cards can be selected; **whether they are
         * *paused* or not should be checked before calling this function**
         */
        suspend fun reset(
            testTypes: Array<TestType> = TestType.all(),
            folders: Array<UUID> = arrayOf()
        ) {
            counter = 0
            this.testTypes = testTypes
            this.folders = folders
            cards = WordRepository.get().getWordCards().toList()[0].filter { !it.paused && !it.trained() && (it.folderID in folders)}.toMutableList()
        }

        /**
         * Returns [amount] *(or less)* of questions in
         * selected folders of selected types
         */
        fun getQuestions(amount: Int): Array<Question> {
            var questions = arrayOf<Question>()
            for (i in 0..amount) {
                val question = getNextQuestion() ?: return questions
                questions += question
            }
            return questions
        }

        /**
         * Returns not paused folders that include at least 1 card that is not paused, not [WordCard.trained]
         * and can be studied in at least 1 of the selected [TestType]s
         */
        suspend fun aptFolders(testTypes : Array<TestType>) : Array<UUID>{
            val folders = arrayOf<UUID>()
            WordRepository.get().getFolders().toList()[0].forEach {
                if(!it.paused && WordRepository.get().getWordCardsFromFolder2(it.folderID).toList()[0].any { !it.paused && !it.trained() && it.isAptForTrainings(testTypes) }){
                    folders.plus(it.folderID)
                }
            }
            return folders
        }

        /**
         * Checks whether the answer is correct
         *
         * Will always return true for [TestType.Match]
         */
        fun checkAnswer(answers: Array<String>, question: Question): Boolean? {
            val correctAnswers = question.correctAnswers
            var completelyCorrect = true
            for (answer in answers) {
                if (answer !in correctAnswers) {
                    completelyCorrect = false
                    break
                }
            }
            if (completelyCorrect) {
                return true
            }
            var completelyIncorrect = true
            for (answer in answers) {
                if (answer in correctAnswers) {
                    completelyIncorrect = false
                    break
                }
            }
            return if (completelyIncorrect) {
                false
            } else {
                null
            }
        }

        /**
         * Updates [WordCard], including [TrainingHistory] and [WordCard.mastery]
         *
         * @param overruled whether the answer was overruled by the user as correct
         * @see checkAnswer
         */
        fun submitAnswer(answers: Array<String>, question: Question, overruled: Boolean = false) {
            if(question.testType == TestType.Match){
                if (overruled){
                    return
                }
                for (k in 0 until question.wordCards.size){
                    val card = question.wordCards[k]
                    val correct = question.correctAnswers[k] == answers[k]
                    if(correct){
                        for (i in 0 until card.trainingHistory.types.size) {
                            if (card.trainingHistory.types[i].first == question.testType) {
                                card.trainingHistory.types[i] = Pair(question.testType, card.trainingHistory.types[i].second + 3)
                                break
                            }
                        }
                        card.mastery += UserSettings.settings().correctAnswerStep
                        if(card.mastery.toInt() - (card.mastery - UserSettings.settings().correctAnswerStep).toInt() > 0){
                            card.trainingHistory.lastDate = Date()
                        }
                    }
                    else {
                        for (i in 0 until card.trainingHistory.types.size) {
                            if (card.trainingHistory.types[i].first == question.testType) {
                                card.trainingHistory.types[i] = Pair(question.testType, card.trainingHistory.types[i].second - 1)
                                card.mastery -= UserSettings.settings().incorrectAnswerStep
                                if(card.mastery.toInt() - (card.mastery - UserSettings.settings().incorrectAnswerStep).toInt() > 0){
                                    card.adjustLastDate()
                                }
                                break
                            }
                        }
                    }

                }
            }
            else{
                val card = question.wordCards[0]
                val correct = checkAnswer(answers, question) ?: false
                if(overruled || correct) {
                    for (i in 0 until card.trainingHistory.types.size) {
                        if (card.trainingHistory.types[i].first == question.testType) {
                            card.trainingHistory.types[i] = Pair(question.testType, card.trainingHistory.types[i].second + 3)
                            break
                        }
                    }
                    card.mastery += UserSettings.settings().correctAnswerStep
                    if(card.mastery.toInt() - (card.mastery - UserSettings.settings().correctAnswerStep).toInt() > 0){
                        card.trainingHistory.lastDate = Date()
                    }
                }
                else {
                    for (i in 0 until card.trainingHistory.types.size) {
                        if (card.trainingHistory.types[i].first == question.testType) {
                            card.trainingHistory.types[i] = Pair(question.testType, card.trainingHistory.types[i].second - 1)
                            card.mastery -= UserSettings.settings().incorrectAnswerStep
                            if(card.mastery.toInt() - (card.mastery - UserSettings.settings().incorrectAnswerStep).toInt() > 0){
                                card.adjustLastDate()
                            }
                            break
                        }
                    }
                }
            }
        }

        private fun getCard(): WordCard? {
            if(cards.isEmpty()){
                return null
            }
            if (counter >= cards.size) {
                counter = 0
            }
            return cards[counter]
        }

        /**
         * Returns a [Question] for the next [WordCard] if possible
         *
         * TODO: test
         */
        private fun getNextQuestion(): Question? {
            val card = getCard() ?: return null
            counter++
            return try {
                getNextQuestion(card, card.aptTraining(testTypes))
            } catch (e: Exception) {
                println("Error(getNextQuestion): $e\n${e.stackTrace}")
                cards.remove(card)
                getNextQuestion()
            }
        }

        private fun getNextQuestion(card: WordCard, testType: TestType): Question? {
            when (testType) {
                TestType.FlashCard -> {
                    val question = Question(arrayOf(card), testType = TestType.FlashCard)
                    question.options = arrayOf("Continue")
                    question.correctAnswers = arrayOf("Continue")
                    return question
                }

                TestType.TrueFalse -> {
                    val question = Question(arrayOf(card), testType = TestType.TrueFalse)
                    val answer = Random().nextBoolean()
                    question.correctAnswers = arrayOf(if(answer) "True" else "False")
                    question.displayTexts = arrayOf(card.wordString(),(if(answer) card.definition else card.getNotSynonymicDefinitions(1, cards).first()))
                    question.options = arrayOf("True", "False")
                    question.displayTextOnAnsweredWrong = arrayOf(card.wordString(), card.definition)
                    return question
                }

                TestType.MultipleChoiceWord -> {
                    val question = Question(arrayOf(card), testType = TestType.MultipleChoiceWord)
                    question.correctAnswers = arrayOf(card.wordString())
                    question.displayTexts = arrayOf(card.definition)
                    question.options = card.getNotSynonymicWords(UserSettings.settings().questionOptions, cards)
                    if(card.getHintExamples().isNotEmpty()){
                        question.displayTextHint = arrayOf(card.getHintExamples()[card.mastery.toInt() % card.getHintExamples().size])
                    }
                    question.displayTextOnAnsweredWrong = arrayOf(card.wordString(), card.definition)
                    return question
                }

                TestType.MultipleChoiceDefinition -> {
                    val question = Question(arrayOf(card), testType = TestType.MultipleChoiceDefinition)
                    question.correctAnswers = arrayOf(card.definition)
                    question.displayTexts = arrayOf(card.wordString())
                    question.options = card.getNotSynonymicDefinitions(UserSettings.settings().questionOptions, cards)
                    if(card.getHintExamples().isNotEmpty()){
                        question.displayTextHint = arrayOf(card.getHintExamples()[card.mastery.toInt() % card.getHintExamples().size])
                    }
                    question.displayTextOnAnsweredWrong = arrayOf(card.wordString(), card.definition)
                    return question
                }

                TestType.Match -> {
                    val questions = getMatchQuestions(card, cards, UserSettings.settings().matchOptions)
                    if (questions.isEmpty()) {
                        val types = testTypes.toMutableList()
                        types.remove(TestType.Match)
                        val testTypes = card.aptTrainings(types.toTypedArray())
                        return if(testTypes.isEmpty()) {
                            getNextQuestion()
                        }else {
                            getNextQuestion(card, testTypes[0])
                        }
                    }
                    val question = Question(arrayOf(), testType = TestType.Match)
                    for (q in questions){
                        question.wordCards += q.wordCards
                        question.correctAnswers += q.correctAnswers
                        question.displayTexts += q.displayTexts
                        question.options += q.options
                        question.displayTextHint += q.displayTextHint
                        //question.displayTextOnAnsweredWrong
                        //would take too much space
                    }
                    question.options = question.options.toMutableList().shuffled().toTypedArray()
                    return question
                }

                TestType.Synonyms -> {
                    val question = Question(arrayOf(card), testType = TestType.Synonyms)
                    question.correctAnswers =
                        arrayOf(card.synonyms[card.mastery.toInt() % card.synonyms.size])
                    question.displayTexts = arrayOf(card.wordString(), card.definition)
                    question.displayTextHint = card.antonyms
                    question.options = card.getNotSynonymicWords(UserSettings.settings().matchOptions, cards)
                    question.displayTextOnAnsweredWrong = arrayOf(card.wordString(), card.definition) + card.synonyms
                    return question
                }

                TestType.Antonyms -> {
                    val question = Question(arrayOf(card), testType = TestType.Antonyms)
                    question.correctAnswers =
                        arrayOf(card.antonyms[card.mastery.toInt() % card.antonyms.size])
                    question.displayTexts = arrayOf(card.wordString(), card.definition)
                    question.displayTextHint = card.synonyms
                    question.options = card.getNotAntonymousWords(UserSettings.settings().matchOptions, cards)
                    question.displayTextOnAnsweredWrong = arrayOf(card.wordString(), card.definition) + card.antonyms
                    return question
                }

                TestType.Writing -> {
                    val question = Question(arrayOf(card), testType = TestType.Writing)
                    question.correctAnswers = arrayOf(card.wordString())
                    question.displayTexts = arrayOf(card.definition)
                    question.displayTextHint =
                        arrayOf(card.getHintExamples()[card.mastery.toInt() % card.getHintExamples().size])
                    question.displayTextOnAnsweredWrong = arrayOf(card.wordString(), card.definition)
                    return question
                }

                TestType.WritingListening -> {
                    val question = Question(arrayOf(card), testType = TestType.WritingListening)
                    question.correctAnswers = arrayOf(card.wordString())
                    question.displayTexts = arrayOf(card.word().audioLinks[0])
                    question.displayTextHint = arrayOf(card.definition)
                    question.displayTextOnAnsweredWrong = arrayOf(card.wordString(), card.word().audioLinks[0])
                    return question
                }

                TestType.NONE -> {
                    cards.remove(card)
                    return getNextQuestion()
                }
            }
        }

        /**
         * Returns an [amount] of match questions for [mainCard] and [cards]
         */
        private fun getMatchQuestions(mainCard: WordCard, cards: MutableList<WordCard>, amount: Int): Array<Question> {
            if(cards.size < amount){
                return arrayOf()
            }
            cards.minus(mainCard)
            cards.sortBy { it.trainingHistory.types[TestType.Match.ordinal].second }
            cards.add(0, mainCard)

            val questions = mutableListOf<Question>()
            for (i in 0 until amount){
                val card = cards[i]
                val question = Question(arrayOf(card), testType = TestType.Match)

                question.displayTexts = arrayOf(card.definition)
                question.correctAnswers = arrayOf(card.wordString())
                question.options = arrayOf(card.wordString())
                question.displayTextHint = arrayOf(card.getHintExamples()[card.mastery.toInt() % card.getHintExamples().size])
                questions.add(question)
            }

            return questions.toTypedArray()
        }
    }
}

