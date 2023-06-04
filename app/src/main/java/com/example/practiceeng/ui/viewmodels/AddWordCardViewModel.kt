package com.example.practiceeng.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.practiceeng.Word
import com.example.practiceeng.database.WordRepository
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
    var name: String
    var pos: String? = null
    var def: String? = null
    var example: MutableList<String> = mutableListOf()
    var synonyms: MutableList<String> = mutableListOf()
    var antonyms: MutableList<String> = mutableListOf()
    var folder: UUID? = null
    var id:UUID? = null
    var word:Word? = null

    init {
        this.name = name
        pos?.let { this.pos = pos }
        def?.let { this.def = def }
        if (!example.isNullOrEmpty()) this.example = example.toMutableList()
        if (!synonyms.isNullOrEmpty()) this.synonyms = synonyms.toMutableList()
        if (!antonyms.isNullOrEmpty()) this.antonyms = antonyms.toMutableList()
        folder?.let { this.folder = folder }
        id?.let { this.id = id }
    }

fun addWordCard(){
    WordRepository.get().addWordCard(pos!!,def!!,example.toTypedArray(),synonyms.toTypedArray(),antonyms.toTypedArray(), name,folder!!)
}

    // TODO: Implement the ViewModel
}