package com.example.practiceeng

/**
 * Types of tests that can be taken
 */
enum class TestType {
    FlashCard,
    TrueFalse,
    /**
     * Definition with [Word] options
     */
    MultipleChoiceWord,
    /**
     * [Word] with definition options
     */
    MultipleChoiceDefinition,
    Match,
    Synonyms,
    Antonyms,
    Writing,
    WritingListening,
    NONE;
    companion object {
        fun all() : Array<TestType>{
            return arrayOf(
                FlashCard,
                TrueFalse,
                MultipleChoiceWord,
                MultipleChoiceDefinition,
                Match,
                Synonyms,
                Antonyms,
                Writing,
                WritingListening
            )
        }
    }
}

enum class DictionaryAPI {
    /**
     * Free Dictionary API with unlimited requests
     * @see <a href="https://dictionaryapi.dev/">Free Dictionary API</a>
     */
    FreeDictionaryAPI,
    /**
     * WordsAPI with **2500** free requests per day
     * @see <a href="https://www.wordsapi.com/">WordsAPI</a>
     */
    WordsAPI,
    /**
     * API with **10.000** free requests per day
     * @see <a href="https://www.xfd.plus/">XF English Dictionary</a>
     */
    XFEnglishDictionary,
    ALL
}

class Utils{
    companion object {
        private val verbs = arrayOf("murmur", "brush", "soothe", "scream", "sigh", "whisper",
            "growl", "invest", "satisfy", "suffer", "lend")
        private val adjectives = arrayOf("abrupt", "acidic", "grumpy", "extraordinary", "fancy",
            "callous", "diligent", "eager", "faithful")
        private val adverbs = arrayOf("hence", "likewise", "accidentally", "seldom",
            "therefore", "foolishly", "undoubtedly", "unfortunately", "upside-down")
        private val other = arrayOf("squall", "out of the blue", "indefinitely", "flagship",
            "soil", "fence", "monstrosity")
        /**
         * Returns an [amount] of semi-random word options
         */
        fun getGeneralWordOptions(amount: Int,
                                  excludedWords: Array<String>,
                                  typeOfSpeech: String) : Array<String> {
            val words = when(typeOfSpeech){
                "verb" -> {
                    verbs.toMutableList().minus(excludedWords.toSet()).toMutableList()
                }

                "adjective" -> {
                    adjectives.toMutableList().minus(excludedWords.toSet()).toMutableList()
                }

                "adverb" -> {
                    adverbs.toMutableList().minus(excludedWords.toSet()).toMutableList()
                }

                else -> {
                    other.toMutableList().minus(excludedWords.toSet()).toMutableList()
                }
            }
            return words.shuffled().take(amount).toTypedArray()
        }

        fun getGeneralDefinitionOptions(amount: Int,
                                        excludedWords: Array<String>,
                                        partOfSpeech: String): Array<String> {
            return TODO()
        }
    }
}