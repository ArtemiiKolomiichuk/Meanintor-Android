package com.example.practiceeng

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
        private var folders: Array<String> = arrayOf<String>()
        private var testTypes: Array<TestType> = arrayOf<TestType>()
        private var cards: Array<WordCard> = arrayOf<WordCard>()
        var counter: Int = 0

        /**
         * Resets the question manager
         * @param testTypes the types of tests that can be selected
         * @param folders folders from which cards can be selected; **whether they are
         * *paused* or not should be checked before calling this function**
         */
        fun reset(
            testTypes: Array<TestType> = Array<TestType>(1) { TestType.ALL },
            folders: Array<String> = arrayOf<String>()
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
         * Updates [WordCard], including [TrainingHistory] and [WordCard.mastery]
         */
        fun submitAnswer(answers: Array<String>, question: Question) : Boolean {
            val card = question.wordCard
            val correctAnswers = question.correctAnswers
            var correct = true
            for (answer in answers) {
                if (answer !in correctAnswers) {
                    correct = false
                    break
                }
            }
            for (answer in correctAnswers) {
                if (answer !in answers) {
                    correct = false
                    break
                }
            }
            if(correct) {
                for (i in 0 until card.trainingHistory.types.size) {
                    if (card.trainingHistory.types[i].first == question.testType) {
                        card.trainingHistory.types[i] = Pair(question.testType, card.trainingHistory.types[i].second + 1)
                        break
                    }
                }
                card.trainingHistory.lastDate = Date()
            }
            else {
                //TODO: may be overruled manually by user
                for (i in 0 until card.trainingHistory.types.size) {
                    if (card.trainingHistory.types[i].first == question.testType) {
                        card.trainingHistory.types[i] = Pair(question.testType, card.trainingHistory.types[i].second - 1)
                        break
                    }
                }
            }
            return correct
        }

        private fun getNextCard(): WordCard {
            if (counter >= cards.size) {
                counter = 0
            }
            return cards[counter]
        }

        private fun getNextQuestion(): Question? {
            val card = getNextCard()
            counter++
            when (card.aptTraining(testTypes)) {
                TestType.FlashCard -> TODO()
                TestType.TrueFalse -> {
                    var question = Question(card, testType = TestType.TrueFalse)
                    //question.correctAnswers =
                    //question.displayTexts =
                    question.options = arrayOf("True", "False")
                    //question.displayTextHint =
                    //question.displayTextOnAnsweredWrong
                    return TODO()
                }
                TestType.MultipleChoiceWord -> {
                    var question = Question(card, testType = TestType.MultipleChoiceWord)
                    question.correctAnswers = arrayOf(card.word())
                    question.displayTexts = arrayOf(card.definition)
                    question.options = card.getNotSynonymicWords(TODO())
                    //question.displayTextHint = arrayOf(card.getHintExamples()[card.mastery.toInt() % card.getHintExamples().size])
                    //TODO: question.displayTextOnAnsweredWrong
                    return question
                }
                TestType.MultipleChoiceDefinition -> {
                    var question = Question(card, testType = TestType.MultipleChoiceDefinition)
                    question.correctAnswers = arrayOf(card.definition)
                    question.displayTexts = arrayOf(card.word())
                    question.options = card.getNotSynonymicDefinitions(TODO())
                    //question.displayTextHint = arrayOf(card.getHintExamples()[card.mastery.toInt() % card.getHintExamples().size])
                    //TODO: question.displayTextOnAnsweredWrong = card.definitions
                    return question
                }
                TestType.Match -> {
                    var question = Question(card, testType = TestType.Match)
                    return TODO()
                    //multiple cards at once
                }
                TestType.Synonyms -> {
                    var question = Question(card, testType = TestType.Synonyms)
                    question.correctAnswers = arrayOf(card.synonyms[card.mastery.toInt() % card.synonyms.size])
                    question.displayTexts = arrayOf(card.word())
                    //question.displayTextHint = arrayOf(card.getHintExamples()[card.mastery.toInt() % card.getHintExamples().size])
                    question.options = card.getNotSynonymicWords(TODO())
                    //TODO: question.displayTextOnAnsweredWrong = card.synonyms ?
                    return question
                }
                TestType.Antonyms -> {
                    var question = Question(card, testType = TestType.Antonyms)
                    question.correctAnswers = arrayOf(card.antonyms[card.mastery.toInt() % card.antonyms.size])
                    question.displayTexts = arrayOf(card.word())
                    //question.displayTextHint = arrayOf(card.getHintExamples()[card.mastery.toInt() % card.getHintExamples().size])
                    question.options = card.getNotAntonymousWords(TODO())
                    //TODO: question.displayTextOnAnsweredWrong = card.antonyms ?
                    return question
                }
                TestType.Writing -> {
                    var question = Question(card, testType = TestType.Writing)
                    question.correctAnswers = arrayOf(card.word())
                    question.displayTexts = arrayOf(card.definition)
                    question.displayTextHint = arrayOf(card.getHintExamples()[card.mastery.toInt() % card.getHintExamples().size])
                    //TODO: question.displayTextOnAnsweredWrong
                    return question
                }
                TestType.WritingListening -> {
                    var question = Question(card, testType = TestType.WritingListening)
                    question.correctAnswers = arrayOf(card.word())
                    question.displayTexts = arrayOf(card.audioLink() ?: "")
                    question.displayTextHint = arrayOf(card.getHintExamples()[card.mastery.toInt() % card.getHintExamples().size])
                    //TODO: question.displayTextOnAnsweredWrong
                    return question
                }
                TestType.ALL -> {
                    return getNextQuestion()
                }
                TestType.NONE -> {
                    return null
                }
            }
        }
    }
}

