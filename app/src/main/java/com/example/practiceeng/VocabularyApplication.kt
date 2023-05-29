package com.example.practiceeng

import android.app.Application
import com.example.practiceeng.database.WordRepository

class VocabularyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        WordRepository.initialize(this)
    }
}