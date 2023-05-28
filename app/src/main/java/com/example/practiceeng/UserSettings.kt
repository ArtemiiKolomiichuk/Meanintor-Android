package com.example.practiceeng

data class UserSettings
private constructor(
    val masteredMargin : Double = 7.0,
    val questionOptions : Int = 4,
    val matchOptions : Int = 5,
    val correctAnswerStep : Double = 0.2,
    val incorrectAnswerStep: Double = 0.4)
{
    companion object {
        fun settings(): UserSettings {
            return UserSettings()
        }
    }
}

