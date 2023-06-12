package com.example.practiceeng.ui.viewmodels

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.practiceeng.*
import com.example.practiceeng.database.WordRepository
import java.util.*

class TrainingFragmentViewModelFactory(val amount:Int, val types: BooleanArray, val folders: UUID?)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val testTypes = mutableListOf<TestType>()
        TestType.all().forEachIndexed { index, testType ->
            if (types[index])
                testTypes += testType
        }
        return TrainingFragmentViewModel(amount, testTypes.toTypedArray(), folders) as T
    }
}
class TrainingFragmentViewModel(val amount:Int, val types: Array<TestType>, val folder: UUID?) : ViewModel() {
    var currentLayout: View? = null
    var currentIndex: Int = -1
    private var _questionBank: MutableList<Question>? = null
    val questionBank get() = _questionBank as List<Question>?
    var lastMatching: String? = null

    var writingField: String = ""
    val folderID = folder

    fun updateQuestionBank(wordCards: List<WordCard>) {
        _questionBank = QuestionManager.getQuestions(
            amount,
            wordCards.filter { !(it.paused || it.trained()) } as MutableList<WordCard>,
            types).toMutableList()
    }

    fun hasNext(): Boolean {
        return currentIndex + 1 < _questionBank?.size ?:0
    }

    fun size(): Int = _questionBank?.size ?:0

    fun moveToNext(): Boolean {
        if (!hasNext())
            return false
        currentIndex = currentIndex + 1
        return true
    }
    fun pushCurrentToEnd() {
        _questionBank?.removeAt(currentIndex--)?.let { _questionBank?.add(it) }
    }

    fun index():Int {return currentIndex}
    fun currentWordCards(): Array<WordCard> = _questionBank!![currentIndex].wordCards
    fun currentDisplayTexts(): Array<String> = _questionBank!![currentIndex].displayTexts
    fun currentDisplayTextHint(): Array<String> = _questionBank!![currentIndex].displayTextHint
    fun currentDisplayTextOnAnsweredWrong(): Array<String> = _questionBank!![currentIndex].displayTextOnAnsweredWrong
    fun currentCorrectAnswers(): Array<String> = _questionBank!![currentIndex].correctAnswers
    fun currentOptions(): Array<String> = _questionBank!![currentIndex].options
    fun currentTestType(): TestType = _questionBank!![currentIndex].testType
    fun isNotEmpty(): Boolean = size()>0

    fun updateCardsInfo(cards:Array<UUID>,correctness: Array<Boolean>){
       WordRepository.get().updateCardsInfo(cards,correctness, currentTestType());
    }
}