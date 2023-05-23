package com.example.practiceeng

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
            //cards = TODO: DataBase: load cards, not paused
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
         * Returns the maximum amount of questions
         * that can be generated based on the current settings
         */
        fun maxAmount(): Int {
            var amount = 0
            while (getNextQuestion() != null) {
                amount++
            }
            return amount
        }

        /**
         * Checks whether the answer is correct
         *
         * Updates [WordCard], including [TrainingHistory] and [WordCard.mastery]
         */
        fun submitAnswer(answers: Array<String>, question: Question) {
            TODO()
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
                TestType.TrueFalse -> TODO()
                TestType.MultipleChoiceWord -> TODO()
                TestType.MultipleChoiceDefinition -> TODO()
                TestType.Match -> TODO()
                TestType.Synonyms -> TODO()
                TestType.Antonyms -> TODO()
                TestType.Writing -> TODO()
                TestType.WritingListening -> TODO()
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

