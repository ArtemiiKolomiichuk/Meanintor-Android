package com.example.practiceeng.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.practiceeng.Folder
import com.example.practiceeng.database.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class ChooseFolderViewModelFactory(var folder: UUID?) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChooseFolderViewModel(folder) as T
    }
}


class ChooseFolderViewModel(folder: UUID?) : ViewModel() {

    private val _foldersList: MutableStateFlow<List<Folder>> = MutableStateFlow(emptyList())
    val foldersList:StateFlow<List<Folder>>
        get() = _foldersList.asStateFlow()
    var position:Int = -1
    var folderID : UUID? = folder

    init {
        viewModelScope.launch {
            WordRepository.get().getFolders().collect { _foldersList.value=it}
            }
    }

    suspend fun addFolder(folder:Folder){
        WordRepository.get().addFolder(folder)
    }
}