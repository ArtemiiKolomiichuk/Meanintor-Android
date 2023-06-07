package com.example.practiceeng.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.practiceeng.R
import com.example.practiceeng.TestType
import com.example.practiceeng.TrainingHistory
import com.example.practiceeng.database.WordRepository
import com.example.practiceeng.databinding.ActivityMainBinding
import com.example.practiceeng.ui.fragments.SettingsFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    val binding get() = _binding!!
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = Navigation.findNavController(this, R.id.fragment_container)
        setupWithNavController(binding.bottomNavigationView, navController)
    }
}