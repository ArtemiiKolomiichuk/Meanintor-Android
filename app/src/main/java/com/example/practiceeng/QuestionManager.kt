package com.example.practiceeng

import java.lang.Exception
import java.util.Date

/**
 * Manages the questions
 * @see reset
 * @see getQuestions
 * @see submitAnswer
 */
class QuestionManager
{
    companion object {
        private var folders: Array<String> = arrayOf()
        private var testTypes: Array<TestType> = arrayOf()
        private var cards: Array<WordCard> = arrayOf()
        var counter: Int = 0

        /**
         * Resets the question manager
         * @param testTypes the types of tests that can be selected
         * @param folders folders from which cards can be selected; **whether they are
         * *paused* or not should be checked before calling this function**
         */
        fun reset(
            testTypes: Array<TestType> = TestType.values().copyOfRange(0, TestType.values().size - 1),
            folders: Array<String> = arrayOf()
        ) {
            counter = 0
            this.testTypes = testTypes
            this.folders = folders
            //cards = TODO: DataBase: load cards, not paused from folders
        }

        /**
         * Return [amount] *(or less)* of questions in
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
         * Returns folders that include at least 1 card that is not paused
         * and can be studied in at least 1 of the selected [TestType]s
         */
        fun aptFolders(testTypes : Array<TestType>) : Array<String>{
              return TODO()
        }

        /**
         * Checks whether the answer is correct
         *
         * @return **null** for [TestType.Match], [TestType.Synonyms] and [TestType.Antonyms] if the answer is not completely correct/incorrect
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
                                //TODO: adjust lastDate to avoid constant mastery level decrease
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
                            //TODO: adjust lastDate to avoid constant mastery level decrease
                            break
                        }
                    }
                }
            }
        }

        private fun getNextCard(): WordCard {
            if (counter >= cards.size) {
                counter = 0
            }
            return cards[counter]
        }

        /**
         * TODO: check for infinite loop possibilities
         */
        private fun getNextQuestion(): Question? {
            val card = getNextCard()
            counter++
            return try {
                getNextQuestion(card, card.aptTraining(testTypes))
            } catch (e: Exception) {
                println("Error(getNextQuestion): $e")
                getNextQuestion()
            }
        }

        private fun getNextQuestion(card: WordCard, testType: TestType): Question? {
            when (testType) {
                TestType.FlashCard -> TODO()
                TestType.TrueFalse -> {
                    val question = Question(arrayOf(card), testType = TestType.TrueFalse)
                    //question.correctAnswers =
                    //question.displayTexts =
                    question.options = arrayOf("True", "False")
                    //question.displayTextHint =
                    //question.displayTextOnAnsweredWrong
                    return TODO()
                }

                TestType.MultipleChoiceWord -> {
                    val question = Question(arrayOf(card), testType = TestType.MultipleChoiceWord)
                    question.correctAnswers = arrayOf(card.word())
                    question.displayTexts = arrayOf(card.definition)
                    question.options = card.getNotSynonymicWords(TODO())
                    //question.displayTextHint = arrayOf(card.getHintExamples()[card.mastery.toInt() % card.getHintExamples().size])
                    //TODO: question.displayTextOnAnsweredWrong
                    return question
                }

                TestType.MultipleChoiceDefinition -> {
                    val question = Question(arrayOf(card), testType = TestType.MultipleChoiceDefinition)
                    question.correctAnswers = arrayOf(card.definition)
                    question.displayTexts = arrayOf(card.word())
                    question.options = card.getNotSynonymicDefinitions(TODO())
                    //question.displayTextHint = arrayOf(card.getHintExamples()[card.mastery.toInt() % card.getHintExamples().size])
                    //TODO: question.displayTextOnAnsweredWrong = card.definitions
                    return question
                }

                TestType.Match -> {
                    val questions = getMatchQuestions(cards, UserSettings.settings().matchOptions)
                    if (questions.isNullOrEmpty()) {
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
                        //question.displayTextOnAnsweredWrong TODO:???
                    }
                    return question
                }

                TestType.Synonyms -> {
                    val question = Question(arrayOf(card), testType = TestType.Synonyms)
                    question.correctAnswers =
                        arrayOf(card.synonyms[card.mastery.toInt() % card.synonyms.size])
                    question.displayTexts = arrayOf(card.word(), card.definition)
                    //question.displayTextHint = arrayOf(card.getHintExamples()[card.mastery.toInt() % card.getHintExamples().size])
                    question.options = card.getNotSynonymicWords(TODO())
                    //TODO: question.displayTextOnAnsweredWrong = card.synonyms ?
                    return question
                }

                TestType.Antonyms -> {
                    val question = Question(arrayOf(card), testType = TestType.Antonyms)
                    question.correctAnswers =
                        arrayOf(card.antonyms[card.mastery.toInt() % card.antonyms.size])
                    question.displayTexts = arrayOf(card.word(), card.definition)
                    //question.displayTextHint = arrayOf(card.getHintExamples()[card.mastery.toInt() % card.getHintExamples().size])
                    question.options = card.getNotAntonymousWords(TODO())
                    //TODO: question.displayTextOnAnsweredWrong = card.antonyms ?
                    return question
                }

                TestType.Writing -> {
                    val question = Question(arrayOf(card), testType = TestType.Writing)
                    question.correctAnswers = arrayOf(card.word())
                    question.displayTexts = arrayOf(card.definition)
                    question.displayTextHint =
                        arrayOf(card.getHintExamples()[card.mastery.toInt() % card.getHintExamples().size])
                    //TODO: question.displayTextOnAnsweredWrong
                    return question
                }

                TestType.WritingListening -> {
                    val question = Question(arrayOf(card), testType = TestType.WritingListening)
                    question.correctAnswers = arrayOf(card.word())
                    //question.displayTexts = card.audioLinks
                    question.displayTextHint =
                        arrayOf(card.getHintExamples()[card.mastery.toInt() % card.getHintExamples().size])
                    //TODO: question.displayTextOnAnsweredWrong
                    return question
                }

                TestType.NONE -> {
                    return getNextQuestion()
                }
            }
        }

        private fun getMatchQuestions(cards: Array<WordCard>, amount: Int): Array<Question>? {
            return TODO()
        }
    }
}

