package com.example.practiceeng

/**
 * Universal data class that represents
 * a question of any [TestType]
 */
data class Question
public constructor(
    val displayTexts : Array<String> = arrayOf<String>(),
    val displayTextsSecondary : Array<String> = arrayOf<String>(),
    val correctAnswers : Array<String> = arrayOf<String>(),
    var options : Array<String> = arrayOf<String>(),
    val testType: TestType)
{
}

