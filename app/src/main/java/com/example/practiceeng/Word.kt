package com.example.practiceeng

import java.util.UUID

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
    var bookmarked: Boolean = false,
    var wordID : UUID = UUID.randomUUID())
{

override fun toString(): String {
    return "<$word>\n" +
            "Phonetics: ${phonetics.joinToString("# ")}\n" +
            "Audio Links: ${audioLinks.joinToString("# ")}\n"
}
}

