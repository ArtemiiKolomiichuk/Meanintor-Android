package com.example.practiceeng.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.*


class TrainingSetupViewModelFactory(var folderId: UUID?) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TrainingSetupViewModel(folderId) as T
    }
}

class TrainingSetupViewModel(val folderId:UUID?): ViewModel() {

    var maxWords:Int = 15
    val typesBooleanArray: BooleanArray = booleanArrayOf(false,false,false,false,false,false,false,false,false)





}