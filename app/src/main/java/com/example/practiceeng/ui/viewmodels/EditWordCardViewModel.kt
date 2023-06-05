package com.example.practiceeng.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.practiceeng.WordCard
import com.example.practiceeng.database.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

class EditWordCardViewModelFactory(var id: UUID) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EditWordCardViewModel(id) as T
    }
}


class EditWordCardViewModel(id: UUID) : ViewModel() {

    var scrollPosition: Int = 0
    private val crimeRepository = WordRepository.get()
    private val _wordCard:MutableStateFlow<WordCard?> = MutableStateFlow(null)
    val wordCard : StateFlow<WordCard?> = _wordCard.asStateFlow()
    var example: MutableList<String> = mutableListOf()
    var synonyms: MutableList<String> = mutableListOf()
    var antonyms: MutableList<String> = mutableListOf()

    init {
        viewModelScope.launch {
            _wordCard.value = crimeRepository.getWordCard(id)
        }
    }

    fun updateCrime(onUpdate: (WordCard) -> WordCard) {
        _wordCard.update { oldCard ->
            oldCard?.let { onUpdate(it) }
        }
    }
    fun saveWordCard() {
        _wordCard.update { oldCard ->
            oldCard?.let { it.copy(antonyms = antonyms.toTypedArray(), synonyms = synonyms.toTypedArray(), examples = example.toTypedArray()) }
        }
        wordCard.value?.let { crimeRepository.updateWordCard(it) }
    }

    fun deleteWordCard() {
        wordCard.value?.let { crimeRepository.deleteWordCard(it.cardID) }
    }
}