package com.example.practiceeng.ui.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.practiceeng.R
import com.example.practiceeng.ui.viewmodels.ChooseFolderViewModel

class ChooseFolderFragment : Fragment() {

    companion object {
        fun newInstance() = ChooseFolderFragment()
    }

    private lateinit var viewModel: ChooseFolderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_choose_folder, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChooseFolderViewModel::class.java)
        // TODO: Use the ViewModel
    }

}