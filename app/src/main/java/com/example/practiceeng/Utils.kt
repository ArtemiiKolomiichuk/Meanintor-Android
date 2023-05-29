package com.example.practiceeng

/**
 * Types of tests that can be taken
 */
enum class TestType {
    FlashCard,
    TrueFalse,
    MultipleChoiceWord,
    MultipleChoiceDefinition,
    Match,
    Synonyms,
    Antonyms,
    Writing,
    WritingListening,
    NONE
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