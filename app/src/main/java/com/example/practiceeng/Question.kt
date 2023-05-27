package com.example.practiceeng

/**
 * Universal data class that represents
 * a question of any [TestType]
 */
data class Question
constructor(
    var wordCards: Array<WordCard>,
    /**
     * Main Question text, or link to audio for [TestType.WritingListening]
     */
    var displayTexts : Array<String> = arrayOf(),
    /**
     * Optional hint text that can be displayed
     */
    var displayTextHint : Array<String> = arrayOf(),
    /**
     * Text that is displayed after the user answers incorrectly
     */
    var displayTextOnAnsweredWrong : Array<String> = arrayOf(),
    var correctAnswers : Array<String> = arrayOf(),
    var options : Array<String> = arrayOf(),
    val testType: TestType)

