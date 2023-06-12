package com.example.practiceeng

import android.util.Log
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
        private var testTypes: Array<TestType> = arrayOf()
        private var cards: MutableList<WordCard> = mutableListOf()
        private var counter: Int = 0
        private var ceiling = 300

        /**
         * Returns [amount] *(or less)* of questions in
         * selected folders of selected types
         */
        fun getQuestions(amount: Int, cards: MutableList<WordCard>, testTypes: Array<TestType>): Array<Question> {
            this.testTypes = testTypes
            this.cards = cards
            var questions = mutableListOf<Question>()
            counter = 0
            ceiling = 300
            for (i in 0..amount) {
                val question = getNextQuestion(questions) ?: return questions.toTypedArray()
                questions += question
            }
            return questions.toTypedArray()
        }

        /**
         * Returns not paused folders that include at least 1 card that is not paused, not [WordCard.trained]
         * and can be studied in at least 1 of the selected [TestType]s
         */
        suspend fun aptFolders(testTypes : Array<TestType>) : Array<UUID>{
            val folders = arrayOf<UUID>()
            WordRepository.get().getFolders().toList()[0].forEach {
                if(!it.paused && WordRepository.get().getWordCardsFromFolder(it.folderID).toList()[0].any { !it.paused && !it.trained() && it.isAptForTrainings(testTypes) }){
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

        fun submitAnswer(card: WordCard, correctness : Boolean, testType: TestType) : WordCard{
            val q = Question(arrayOf(card), testType = testType)
            val provided = mutableListOf<String>()
            val needed = mutableListOf<String>()
                provided += if(correctness){
                    "+"
                } else{
                    "-"
                }
                needed += "+"
            q.correctAnswers = needed.toTypedArray()
            submitAnswer(provided.toTypedArray(), q)
            return q.wordCards[0]
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
                    if(card.mastery < 0)
                        card.mastery = 0.0
                    question.wordCards[k] = card
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
                if(card.mastery < 0)
                    card.mastery = 0.0
                question.wordCards[0] = card
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
        private fun getNextQuestion(questions: MutableList<Question>): Question? {
            val card = getCard() ?: return null
            counter++
            ceiling--
            if(ceiling <= 0){
                return null
            }
            return try {
                var types = arrayOf<TestType>()
                for(question in questions){
                    if(question.wordCards.contains(card)){
                        types += (question.testType)
                    }
                }
                types = (testTypes.toMutableList() - types.toSet()).toTypedArray()
                getNextQuestion(card, card.aptTraining(types), questions)
            } catch (e: Exception) {
                println("Error(getNextQuestion): $e\n${e.stackTrace}")
                cards.remove(card)
                getNextQuestion(questions)
            }
        }

        private fun getNextQuestion(card: WordCard, testType: TestType, questions: MutableList<Question>): Question? {
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

                    question.options = card.getNotSynonymicWords(UserSettings.settings().questionOptions - 1, cards)
                    question.options += question.correctAnswers[0];
                    question.options.shuffle()
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
                    question.options = card.getNotSynonymicDefinitions(UserSettings.settings().questionOptions - 1, cards)
                    question.options += question.correctAnswers[0]
                    question.options.shuffle()
                    if(card.getHintExamples().isNotEmpty()){
                        question.displayTextHint = arrayOf(card.getHintExamples()[card.mastery.toInt() % card.getHintExamples().size])
                    }
                    question.displayTextOnAnsweredWrong = arrayOf(card.wordString(), card.definition)
                    return question
                }

                TestType.Match -> {
                    val questions2 = getMatchQuestions(card, cards, UserSettings.settings().matchOptions)
                    if (questions2.isEmpty()) {
                        testTypes = testTypes.toList().minus(TestType.Match).toTypedArray()
                        return getNextQuestion(questions)
                    }
                    val question = Question(arrayOf(), testType = TestType.Match)
                    for (q in questions2){
                        question.wordCards += q.wordCards
                        question.correctAnswers += q.correctAnswers
                        question.displayTexts += q.displayTexts
                        question.options += q.options
                        question.displayTextHint += q.displayTextHint
                        //question.displayTextOnAnsweredWrong
                        //would take too much space
                    }
                    question.options = question.options.toMutableList().shuffled().toTypedArray()
                    testTypes = (testTypes.toMutableList() - TestType.Match).toTypedArray()
                    return question
                }

                TestType.Synonyms -> {
                    val question = Question(arrayOf(card), testType = TestType.Synonyms)
                    question.correctAnswers =
                        arrayOf(card.synonyms[card.mastery.toInt() % card.synonyms.size])
                    question.displayTexts = arrayOf(card.wordString(), card.definition)
                    question.displayTextHint = card.antonyms
                    question.options = card.getNotSynonymicWords(UserSettings.settings().questionOptions - 1, cards)
                    question.options += question.correctAnswers[0];
                    question.options.shuffle()
                    question.displayTextOnAnsweredWrong = arrayOf(card.wordString(), card.definition) + card.synonyms
                    return question
                }

                TestType.Antonyms -> {
                    val question = Question(arrayOf(card), testType = TestType.Antonyms)
                    question.correctAnswers =
                        arrayOf(card.antonyms[card.mastery.toInt() % card.antonyms.size])
                    question.displayTexts = arrayOf(card.wordString(), card.definition)
                    question.displayTextHint = card.synonyms
                    question.options = card.getNotAntonymousWords(UserSettings.settings().questionOptions - 1, cards)
                    question.options += question.correctAnswers[0];
                    question.options.shuffle()
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
                    question.displayTexts = arrayOf(card.word().audioLinks[1])
                    question.displayTextHint = arrayOf(card.definition)
                    question.displayTextOnAnsweredWrong = arrayOf(card.wordString(), card.word().audioLinks[0])
                    return question
                }

                TestType.NONE -> {
                    cards.remove(card)
                    return getNextQuestion(questions)
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
            cards.removeIf { it.wordString() == mainCard.wordString() }
            cards.sortBy { it.trainingHistory.types[TestType.Match.ordinal].second }
            cards.add(0, mainCard)

            if(cards.size < amount){
                return arrayOf()
            }
            val questions = mutableListOf<Question>()
            for (i in 0 until amount){
                val card = cards[i]
                val question = Question(arrayOf(card), testType = TestType.Match)

                question.displayTexts = arrayOf(card.definition)
                question.correctAnswers = arrayOf(card.wordString())
                question.options = arrayOf(card.wordString())
                if(card.getHintExamples().isNotEmpty()){
                    question.displayTextHint = arrayOf(card.getHintExamples()[card.mastery.toInt() % card.getHintExamples().size])
                }
                questions.add(question)
            }

            return questions.toTypedArray()
        }
    }
}

