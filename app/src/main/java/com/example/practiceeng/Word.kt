package com.example.practiceeng

/**
 * A word that can have multiple [WordCard] attached to it
 */
data class Word
constructor(
    var word: String,
    /**
     * Phonetic transcription of the word for All, Noun and Verb
     */
    var phonetics: Array<String> = Array<String>(3) {""; ""; ""},
    var bookmarked: Boolean = false)
{


}

