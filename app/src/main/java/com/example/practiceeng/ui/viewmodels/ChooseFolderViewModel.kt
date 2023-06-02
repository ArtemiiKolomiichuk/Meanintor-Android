package com.example.practiceeng.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.practiceeng.Folder
import com.example.practiceeng.database.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChooseFolderViewModel : ViewModel() {

    private val _foldersList: MutableStateFlow<List<Folder>> = MutableStateFlow(emptyList())
    val foldersList:StateFlow<List<Folder>>
        get() = _foldersList.asStateFlow()
    var position:Int = -1

    init {
        viewModelScope.launch {
            WordRepository.get().getFolders().collect { _foldersList.value=it}
            }
    }

    suspend fun addFolder(folder:Folder){
        WordRepository.get().addFolder(folder)
    }
}