package com.example.practiceeng.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practiceeng.CountedFolder
import com.example.practiceeng.Folder
import com.example.practiceeng.database.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class SavedFoldersViewModel : ViewModel() {
    private val _folders: MutableStateFlow<List<CountedFolder>> = MutableStateFlow(emptyList())
    val folders: StateFlow<List<CountedFolder>>
        get() = _folders.asStateFlow()


    init {
        viewModelScope.launch {
            WordRepository.get().getCountedFolders().collect {
                _folders.value = it
                }
            }
    }

}