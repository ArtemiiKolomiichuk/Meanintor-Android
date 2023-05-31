package com.example.practiceeng.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.practiceeng.Word
import com.example.practiceeng.WordCard
import com.example.practiceeng.database.WordRepository
import kotlinx.coroutines.launch
import java.util.*


class AddWordCardViewModelFactory(
    private var name: String,
    private var pos: String?,
    private var def: String?,
     var example: Array<String>? ,
     var synonyms: Array<String>?,
     var antonyms: Array<String>?   ,
     var folder: UUID?,
    val id:UUID?)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddWordCardViewModel(
        name,
        pos,
        def,
        example,
        synonyms,
        antonyms,
        folder,
        id) as T
    }
}

class AddWordCardViewModel(
      name: String,
      pos: String?,
      def: String?,
      example: Array<String>?,
      synonyms: Array<String>?,
      antonyms: Array<String>?,
      folder: UUID?,
      id:UUID?
) : ViewModel() {
    lateinit var name: String
    var pos: String? = null
    var def: String? = null
    var example: Array<String>? = null
    var synonyms: Array<String>? = null
    var antonyms: Array<String>? = null
    var folder: UUID? = null
    var id:UUID? = null
    var word:Word? = null

    init {
        name?.let { this.name = name }
        pos?.let { this.pos = pos }
        def?.let { this.def = def }
        example?.let { this.example = example }
        synonyms?.let { this.synonyms = synonyms }
        antonyms?.let { this.antonyms = antonyms }
        folder?.let { this.folder = folder }
        id?.let { this.id = id }
        viewModelScope.launch {
            word = WordRepository.get().getWord(name)
        }
    }

fun addWordCard(){
    WordRepository.get().addWordCard(WordCard(pos!!,def!!,example!!,synonyms!!,antonyms!!,
        word!!
        ,folder!!))
}

    // TODO: Implement the ViewModel
}