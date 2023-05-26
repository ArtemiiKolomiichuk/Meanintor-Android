package com.example.practiceeng

/**
 * Universal data class that represents
 * a question of any [TestType]
 */
data class Question
public constructor(
    val wordCard: WordCard,
    /**
     * Main Question text, or link to audio for [TestType.WritingListening]
     */
    var displayTexts : Array<String> = arrayOf<String>(),
    /**
     * Optional hint text that can be displayed
     */
    var displayTextHint : Array<String> = arrayOf<String>(),
    /**
     * Text that is displayed after the user answers incorrectly
     */
    var displayTextOnAnsweredWrong : Array<String> = arrayOf<String>(),
    var correctAnswers : Array<String> = arrayOf<String>(),
    var options : Array<String> = arrayOf<String>(),
    val testType: TestType)
{
}

