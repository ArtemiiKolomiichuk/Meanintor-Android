package com.example.practiceeng.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practiceeng.*
import com.example.practiceeng.database.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class QuizViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private var currentIndex: Int = 0
    private val questionBank : MutableList<Question> = mutableListOf()

    private val _cards: MutableStateFlow<List<WordCard>> = MutableStateFlow(emptyList())
    val cards: StateFlow<List<WordCard>>
        get() = _cards.asStateFlow()

    init {
        viewModelScope.launch {
            WordRepository.get().getWordCards().collect {
                _cards.value = it
                it.forEach { /*questionBank.add(Question(arrayOf(it), arrayOf(it.wordString()), arrayOf(), TestType.MultipleChoiceDefinition))*/ }
            }
        }
    }
    /*


    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }*/
}
