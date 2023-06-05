package com.example.practiceeng

/**
 * Types of tests that can be taken
 */
enum class TestType {
    /**
     * Note: display question based directly on question.wordCards[0]
     */
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
            "growl", "invest", "satisfy", "suffer", "lend", "cope with", "flush", "prey", "doubt")
        private val adjectives = arrayOf("abrupt", "acidic", "grumpy", "extraordinary", "fancy",
            "callous", "diligent", "eager", "faithful", "prodigious", "obscene", "ambient")
        private val adverbs = arrayOf("hence", "likewise", "accidentally", "seldom", "fortunately",
            "therefore", "foolishly", "undoubtedly", "unfortunately", "upside-down", "mercilessly")
        private val other = arrayOf("squall", "out of the blue", "indefinitely", "flagship",
            "soil", "fence", "monstrosity", "slum", "craze", "drought", "whiff", "drift off",
            "chunk", "blip", "presence", "rod", "the dead of night", "cut to the chase", "get the hang of")
        private val definitions = arrayOf("to complete something successfully", "not likely to happen in the real world",
            "a fact of something bad happening", "a person who is not very good at doing something",
            "extremely hot", "unnecessarily complicated", "empty and not welcoming",
            "having no effect or not achieving anything", "a person who is not very good at doing something",
            "a short description of a book", "the fact of continuous possession of something",
            "make someone angry", "reduce the value of something", "reminiscent of something")

        /**
         * Returns an [amount] of semi-random word options
         */
        fun getGeneralWordOptions(amount: Int,
                                  excludedWords: Array<String>,
                                  typeOfSpeech: String) : Array<String> {
            return when(typeOfSpeech.lowercase()){
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
            }.shuffled().take(amount).toTypedArray()
        }

        /**
         * Returns an [amount] of semi-random definition options
         */
        fun getGeneralDefinitionOptions(amount: Int): Array<String> {
            return definitions.toMutableList().shuffled().take(amount).toTypedArray()
        }
    }
}