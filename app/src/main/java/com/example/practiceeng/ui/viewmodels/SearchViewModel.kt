package com.example.practiceeng.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.practiceeng.WordCard
import java.util.*

class SearchViewModel : ViewModel() {
        private var list: Array<WordCard>? = null
       fun setList(data: Array<WordCard>){
               list = data
       }
        fun getList() = list
}