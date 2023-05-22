package com.example.practiceeng

data class Question
public constructor(
    val displayText : String = "",
    val displayTextSecret : String = "",
    val correctAnswers : Array<String> = arrayOf<String>(),
    var options : Array<String> = arrayOf<String>())
{
}

