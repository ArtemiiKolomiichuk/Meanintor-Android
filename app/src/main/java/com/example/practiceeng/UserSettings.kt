package com.example.practiceeng

data class UserSettings
private constructor(
    val masteredMargin : Double = 7.0,
    val questionOptions : Int = 4,
    val matchOptions : Int = 5,
    val correctAnswerStep : Double = 0.2,
    val incorrectAnswerStep: Double = 0.4,
    val downgradeMargins: List<Int> = listOf(0, 26, 65, 9 * 24, 17 * 24, 34 * 24, 100 * 24, 220 * 24))
{
    companion object {
        fun settings(): UserSettings {
            return UserSettings()
        }
    }
}

