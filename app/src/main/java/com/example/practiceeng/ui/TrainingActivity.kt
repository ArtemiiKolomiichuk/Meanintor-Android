package com.example.practiceeng.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navArgs
import com.example.practiceeng.R
import com.example.practiceeng.databinding.ActivityTrainingBinding
import com.example.practiceeng.ui.viewmodels.TrainingFragmentViewModel
import com.example.practiceeng.ui.viewmodels.TrainingFragmentViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class TrainingActivity : AppCompatActivity() {
    private var _binding: ActivityTrainingBinding? = null
    val binding get() = _binding!!
    private val args:TrainingActivityArgs by navArgs()

    private val quizViewModel: TrainingFragmentViewModel by viewModels {
        TrainingFragmentViewModelFactory(args.amount, args.testTypes, args.folders)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val size = quizViewModel.size()
                binding.totalQuestionNumber.text = size.toString()

                }
            }
        }
    }