package com.example.practiceeng.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navArgs
import com.example.practiceeng.database.WordRepository
import com.example.practiceeng.databinding.ActivityTrainingBinding
import com.example.practiceeng.ui.viewmodels.TrainingFragmentViewModel
import com.example.practiceeng.ui.viewmodels.TrainingFragmentViewModelFactory
import kotlinx.coroutines.launch


class TrainingActivity : AppCompatActivity() {
    val TAG = "TrainingActivity"
    private var _binding: ActivityTrainingBinding? = null
    val binding get() = _binding!!
    private val args: TrainingActivityArgs by navArgs()
    private val quizViewModel: TrainingFragmentViewModel by viewModels {
        TrainingFragmentViewModelFactory(args.amount, args.testTypes, args.folders)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                if (quizViewModel.folderID == null) {
                    WordRepository.get().getWordCards().collect {
                        quizViewModel.updateQuestionBank(it)
                        setupQuestionActivity()
                    }
                } else {
                    WordRepository.get().getWordCardsFromFolder(quizViewModel.folderID!!).collect {
                        quizViewModel.updateQuestionBank(it)
                        setupQuestionActivity()
                    }
                }
            }
        }
    }

    fun setupQuestionActivity() {
        val size = quizViewModel.size()
        Log.d(TAG, quizViewModel.questionBank.joinToString(", "))
        binding.totalQuestionNumber.text = size.toString()
        if(quizViewModel.moveToNext()){
        }

    }
}
