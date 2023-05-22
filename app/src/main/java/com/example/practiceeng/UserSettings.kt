package com.example.practiceeng

data class UserSettings
private constructor(
    val masteredMargin : Double = 7.0,
    val showExamplesInMultipleChoice : Boolean = true)
{
    companion object {
        fun settings(): UserSettings {
            return UserSettings()
        }
    }
}

