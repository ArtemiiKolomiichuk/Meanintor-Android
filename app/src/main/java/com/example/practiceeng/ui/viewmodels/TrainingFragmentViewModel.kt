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
    private var currentIndex: Int = -1
    private var questionBank: MutableList<Question> = mutableListOf()
    lateinit var job: Job
    var lastMatching: String? = null

    init {
        if (folder == null) {
            job = viewModelScope.launch {
                WordRepository.get().getWordCards().collect {
                    updateQuestionBank(it)
                }
            }
        } else {
            job = viewModelScope.launch {
                WordRepository.get().getWordCardsFromFolder(folder).collect {
                    updateQuestionBank(it)
                }
            }
        }
    }

    suspend fun updateQuestionBank(wordCards: List<WordCard>) {
        QuestionManager.getQuestions(amount,wordCards.filter { !(it.paused || it.trained()) } as MutableList<WordCard>,types)
    }

    fun hasNext(): Boolean {
        return currentIndex + 1 < questionBank.size
    }

    fun moveToNext() {
        if (hasNext())
            currentIndex = currentIndex + 1
    }

    suspend fun size(): Int {
        return questionBank.size
    }

    suspend fun nextQuestion(): Question? {
        job?.join()
        if (!hasNext())
            return null
        moveToNext()
        return questionBank[currentIndex]
    }

    /* fun currentWordCards() : Array<WordCard> = questionBank[currentIndex].wordCards
    fun currentDisplayTexts() : Array<String>  = questionBank[currentIndex].displayTexts
    fun currentDisplayTextHint() : Array<String>  = questionBank[currentIndex].displayTextHint

    fun currentDisplayTextOnAnsweredWrong() : Array<String> = questionBank[currentIndex].displayTextOnAnsweredWrong
    fun  currentCorrectAnswers() : Array<String> = questionBank[currentIndex].correctAnswers
    fun currentOptions() : Array<String> = questionBank[currentIndex].options
    fun currentTestType() : TestType = questionBank[currentIndex].testType*/
}