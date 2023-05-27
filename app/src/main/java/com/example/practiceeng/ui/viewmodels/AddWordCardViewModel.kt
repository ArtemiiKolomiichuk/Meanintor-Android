package com.example.practiceeng.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.practiceeng.WordCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class AddWordCardViewModelFactory(
    private val name: String?,
    private val pos: String?,
    private val def: String?,
    private val example: String?,
    private val synonyms: String?,
    private val antonyms: String?,
    private val folder: String?
)               : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddWordCardViewModel(
        name,
        pos,
        def,
        example,
        synonyms,
        antonyms,
        folder) as T
    }
}

class AddWordCardViewModel(
      name: String?,
      pos: String?,
      def: String?,
      example: String?,
      synonyms: String?,
      antonyms: String?,
      folder: String?
) : ViewModel() {
    var name: String?      = null
    var pos: String?       = null
    var def: String?       = null
    var example: String?   = null
    var synonyms: String?  = null
    var antonyms: String?  = null
    var folder: String?    = null

    init {
        name?.let { this.name = name }
        pos?.let { this.pos = pos }
        def?.let { this.def = def }
        example?.let { this.example = example }
        synonyms?.let { this.synonyms = synonyms }
        antonyms?.let { this.antonyms = antonyms }
        folder?.let { this.folder = folder }
    }

    // TODO: Implement the ViewModel
}