package com.example.practiceeng.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.practiceeng.R
import com.example.practiceeng.databinding.ActivityMainBinding
import com.example.practiceeng.databinding.ActivityTrainingBinding

class TrainingActivity : AppCompatActivity() {
    private var _binding: ActivityTrainingBinding? = null
    val binding get() = _binding!!
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}