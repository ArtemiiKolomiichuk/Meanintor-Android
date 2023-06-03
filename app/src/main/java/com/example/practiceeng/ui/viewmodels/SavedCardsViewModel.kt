package com.example.practiceeng.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.practiceeng.WordCard
import com.example.practiceeng.database.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class SavedCardsViewModelFactory(val folder: UUID)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SavedCardsViewModel(folder) as T
    }
}

class SavedCardsViewModel(folder: UUID) : ViewModel() {
    private val _cards: MutableStateFlow<List<WordCard>> = MutableStateFlow(emptyList())
    val cards: StateFlow<List<WordCard>>
        get() = _cards.asStateFlow()

    init {
        viewModelScope.launch {
            WordRepository.get().getWordCardsFromFolder(folder).collect {
                _cards.value = it
            }
        }
    }

}