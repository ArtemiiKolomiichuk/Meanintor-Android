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
    @PrimaryKey val word: String,
    /**
     * Phonetic transcription of the word with the locale or type of speech
     *
     * e.g. {(UK) ˌɛnəˈdʒɛtɪk}
     */
    var phonetics: Array<String> = arrayOf(),
    /**
     * Locals and links to the audio
     *
     * e.g. [0] = {US}, [1] = {37513_en-us-awe.ogg}
     */
    var audioLinks: Array<String> = arrayOf(),
    var bookmarked: Boolean = false
)

