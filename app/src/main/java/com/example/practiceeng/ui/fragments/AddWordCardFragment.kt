package com.example.practiceeng.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.view.get
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.practiceeng.R
import com.example.practiceeng.databinding.FragmentAddWordCardBinding
import com.example.practiceeng.ui.StringListAdapter
import com.example.practiceeng.ui.viewmodels.AddWordCardViewModel
import com.example.practiceeng.ui.viewmodels.AddWordCardViewModelFactory
import java.util.UUID

class AddWordCardFragment : Fragment() {

    private var _binding: FragmentAddWordCardBinding? = null
    private val binding get() = _binding!!
    private val args: com.example.practiceeng.ui.fragments.AddWordCardFragmentArgs by navArgs()
    private val addWordCardViewModel: AddWordCardViewModel by viewModels {
        AddWordCardViewModelFactory(
            args.name,
            args.partOfSpeech,
            args.definition,
            args.example,
            args.synonyms,
            args.antonyms,
            args.folder,
            args.cardID
        )
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

            isPhrase.setOnCheckedChangeListener { _, isChecked ->
                customPartOfSpeechChecked(isChecked)
            }
            partOfSpeech.doOnTextChanged { text, _, _, _ ->
                addWordCardViewModel.pos = text.toString()
            }
            saveWordCardButton.setOnClickListener {
                addWordCardViewModel.addWordCard()
            }
            chooseFolderButton.setOnClickListener {
               findNavController().navigate(AddWordCardFragmentDirections.chooseFolder())
            }
            customPartOfSpeechChecked(isPhrase.isChecked)
            addWordCardViewModel.name?.let { word.setText(addWordCardViewModel.name) }

            addWordCardViewModel.def?.let { definition.setText(addWordCardViewModel.def) }
            /*addWordCardViewModel.example?.let { example.setText(addWordCardViewModel.example.toString()) }
            addWordCardViewModel.synonyms?.let { synonyms.setText(addWordCardViewModel.synonyms.toString()) }
            addWordCardViewModel.antonyms?.let { antonyms.setText(addWordCardViewModel.antonyms.toString()) }*/
            val examplesAdapter: StringListAdapter=StringListAdapter(requireActivity(), "Examples", addWordCardViewModel.example)
            examplesList.adapter = examplesAdapter
            examplesList.isClickable=true
            val synonymsAdapter: StringListAdapter = StringListAdapter(requireActivity(), "Synonyms", addWordCardViewModel.synonyms)
            synonymsList.adapter = synonymsAdapter
            synonymsList.isClickable=true
            val antonymsAdapter: StringListAdapter = StringListAdapter(requireActivity(), "Antonyms", addWordCardViewModel.antonyms)
            antonymsList.adapter = antonymsAdapter
            antonymsList.isClickable=true
        }

        setFragmentResultListener(
            ChooseFolderFragment.REQUEST_KEY_FOLDER
        ) { requestKey, bundle ->
        addWordCardViewModel.folder = bundle.getSerializable(ChooseFolderFragment.BUNDLE_KEY_FOLDER) as UUID?
        }
    }

    fun customPartOfSpeechChecked(isChecked:Boolean) {
        binding.apply {
            if (isChecked) {
                spinner.visibility = View.GONE
                partOfSpeech.visibility = View.VISIBLE
                partOfSpeech.setText(addWordCardViewModel.pos)
            } else {
                spinner.visibility = View.VISIBLE
                partOfSpeech.visibility = View.GONE
                addWordCardViewModel.pos?.let {
                    val partsOfSpeech = resources.getStringArray(R.array.parts_of_speech)
                    partsOfSpeech.indices.firstOrNull { i: Int ->
                        partsOfSpeech[i].equals(
                            addWordCardViewModel.pos
                        )
                    }?.let { i ->
                        spinner.setSelection(i)
                    }
                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            addWordCardViewModel.pos = partsOfSpeech[position]
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                    }
                    addWordCardViewModel.pos = partsOfSpeech[spinner.selectedItemPosition]
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}