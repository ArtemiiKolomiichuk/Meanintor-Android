package com.example.practiceeng.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.practiceeng.*
import com.example.practiceeng.database.WordRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.internal.wait
import java.util.*

class TrainingFragmentViewModelFactory(val amount:Int, val types: BooleanArray, val folders: UUID?)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val testTypes = mutableListOf<TestType>()
        TestType.values().forEachIndexed { index, testType ->
            if (types[index])
                testTypes += testType
        }
        return TrainingFragmentViewModel(amount, testTypes.toTypedArray(), folders) as T
    }
}
class TrainingFragmentViewModel(val amount:Int, val types: Array<TestType>, val folder: UUID?) : ViewModel() {
    var currentIndex: Int = 0
    var questionBank: Array<Question>? = null
    var lastMatching: String? = null

    var writingField: String = ""
    val folderID = folder

    fun updateQuestionBank(wordCards: List<WordCard>) {
        questionBank = QuestionManager.getQuestions(
            amount,
            wordCards.filter { !(it.paused || it.trained()) } as MutableList<WordCard>,
            types)
    }

    fun hasNext(): Boolean {
        return currentIndex + 1 < questionBank?.size ?:0
    }

    fun size(): Int = questionBank?.size ?:0

    fun moveToNext(): Boolean {
        if (!hasNext())
            return false
        currentIndex = currentIndex + 1
        return true
    }
    fun pushCurrentToEnd() {
    }

    fun index():Int {return currentIndex}
    fun currentWordCards(): Array<WordCard> = questionBank!![currentIndex].wordCards
    fun currentDisplayTexts(): Array<String> = questionBank!![currentIndex].displayTexts
    fun currentDisplayTextHint(): Array<String> = questionBank!![currentIndex].displayTextHint
    fun currentDisplayTextOnAnsweredWrong(): Array<String> = questionBank!![currentIndex].displayTextOnAnsweredWrong
    fun currentCorrectAnswers(): Array<String> = questionBank!![currentIndex].correctAnswers
    fun currentOptions(): Array<String> = questionBank!![currentIndex].options
    fun currentTestType(): TestType = questionBank!![currentIndex].testType
    fun isNotEmpty(): Boolean = size()>0
}