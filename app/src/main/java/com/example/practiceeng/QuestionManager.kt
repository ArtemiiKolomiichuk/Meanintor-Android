package com.example.practiceeng

class QuestionManager
{
    companion object{
        private var folders : Array<String> = arrayOf<String>()
        private var testTypes : Array<TestType> = arrayOf<TestType>()
        private var cards : Array<WordCard> = arrayOf<WordCard>()
        var counter : Int = 0

        fun reset(testTypes: Array<TestType> = Array<TestType>(1){TestType.ALL}, folders: Array<String> = arrayOf<String>()){
            counter = 0
            this.testTypes = testTypes
            this.folders = folders
        }

        fun getQuestions(amount : Int) : Array<Question> {
            TODO()
        }

        private fun getNextCard() : WordCard {
            TODO()
        }

        private fun getNextQuestion() : Question {
            val card = getNextCard()
            counter++
            when(card.aptTraining(testTypes)){
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
            }
        }

    }
}

