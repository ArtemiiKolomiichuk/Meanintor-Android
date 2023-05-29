package com.example.practiceeng

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * A word that can have multiple [WordCard] attached to it
 */
@Entity
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
    @PrimaryKey var wordID : UUID = UUID.randomUUID())
{

override fun toString(): String {
    return "<$word>\n" +
            "Phonetics: ${phonetics.joinToString("# ")}\n" +
            "Audio Links: ${audioLinks.joinToString("# ")}\n"
}
}

