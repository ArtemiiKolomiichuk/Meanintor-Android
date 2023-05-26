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
    var phonetics: Array<String> = arrayOf(),
    /**
     * Audio links for US, UK, AU pronunciations
     */
    var audioLinks: Array<String> = arrayOf(),
    var bookmarked: Boolean = false)
{


}

