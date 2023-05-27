package com.example.practiceeng.ui.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practiceeng.R
import com.example.practiceeng.databinding.FragmentAddWordCardBinding
import com.example.practiceeng.databinding.FragmentWordSearchBinding
import com.example.practiceeng.ui.viewmodels.AddWordCardViewModel
import com.example.practiceeng.ui.viewmodels.AddWordCardViewModelFactory

class AddWordCardFragment : Fragment() {

    private var _binding: FragmentAddWordCardBinding? = null
    private val binding get() = _binding!!
    private val args: AddWordCardFragmentArgs by navArgs()
    private val addWordCardViewModel: AddWordCardViewModel by viewModels {
        AddWordCardViewModelFactory(args.name,
            args.partOfSpeech,
            args.definition,
            args.example,
        args.synonyms,
        args.antonyms,
        args.folder)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddWordCardBinding.inflate(layoutInflater, container, false)
      binding.apply {

      }
        return binding.root   }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            addWordCardViewModel.name?.let { word.setText(addWordCardViewModel.name) }
            addWordCardViewModel.pos?.let {
                //TODO: if pos if among dropdown then choose dropdown, otherwise send to custom

            }
            addWordCardViewModel.def?.let { definition.setText(addWordCardViewModel.def) }
            addWordCardViewModel.example?.let { example.setText(addWordCardViewModel.example) }
            addWordCardViewModel.synonyms?.let { synonyms.setText(addWordCardViewModel.synonyms) }
            addWordCardViewModel.antonyms?.let { antonyms.setText(addWordCardViewModel.antonyms) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}