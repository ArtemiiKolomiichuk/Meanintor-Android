package com.example.practiceeng

/**
 * A word that can have multiple [WordCard] attached to it
 */
data class Word
constructor(
    var word: String,
    var phonetic: String = "",
    var bookmarked: Boolean = false)
{


}

