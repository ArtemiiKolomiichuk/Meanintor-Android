package com.example.practiceeng.ui.fragments

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceeng.R
import com.example.practiceeng.WordCard
import com.example.practiceeng.databinding.FragmentAddWordCardBinding
import com.example.practiceeng.ui.StringListAdapter
import com.example.practiceeng.ui.adapters.StringAdapter
import com.example.practiceeng.ui.viewmodels.EditWordCardViewModel
import com.example.practiceeng.ui.viewmodels.EditWordCardViewModelFactory
import kotlinx.coroutines.launch
import java.util.UUID

class EditWordCardFragment : Fragment() {

    private var _binding: FragmentAddWordCardBinding? = null
    private val binding get() = _binding!!
    private val args: EditWordCardFragmentArgs by navArgs()
    private val editWordCardViewModel: EditWordCardViewModel by viewModels {
        EditWordCardViewModelFactory(
            args.wordCardId
        )
    }

    private lateinit var partsOfSpeech:Array<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddWordCardBinding.inflate(layoutInflater, container, false)
        binding.apply {
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.deleteWordCardButton.visibility = View.GONE
        partsOfSpeech = resources.getStringArray(R.array.parts_of_speech)
        binding.apply {
            viewLifecycleOwner.lifecycleScope.launch{
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                   editWordCardViewModel.wordCard.collect { card->
                        card?.let { updateUi(it) }
                    }
                }
            }
        }
        setFragmentResultListener(
            ChooseFolderFragment.REQUEST_KEY_FOLDER
        ) { requestKey, bundle ->
            editWordCardViewModel.updateCrime { card -> card.copy(folderID = bundle.getSerializable(ChooseFolderFragment.BUNDLE_KEY_FOLDER) as UUID?) }
            updateSaveButton()
        }
    }

    private fun updateUi(card: WordCard) {
        binding.apply {
            word.setText(card.word.word)
            definition.setText(card.definition)

            isPhrase.setOnCheckedChangeListener { _, isChecked ->
                editWordCardViewModel.wordCard.value?.let { customPartOfSpeechChecked(isChecked) }
                updateSaveButton()
            }
            partOfSpeech.doOnTextChanged { text, _, _, _ ->
                editWordCardViewModel.updateCrime { card-> card.copy(partOfSpeech = text.toString()) }
                updateSaveButton()
            }
            definition.doOnTextChanged { text, _, _, _ ->
                editWordCardViewModel.updateCrime { card-> card.copy(definition = text.toString()) }
                updateSaveButton()
            }
            saveWordCardButton.setOnClickListener {
                editWordCardViewModel.saveWordCard()
                findNavController().navigateUp()
            }
            chooseFolderButton.setOnClickListener {
                findNavController().navigate(
                    EditWordCardFragmentDirections.changeFolder(
                        editWordCardViewModel.wordCard.value!!.folderID
                    )
                )
            }
            editWordCardViewModel.example = card.examples.toMutableList()

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    editWordCardViewModel.updateCrime { card -> card.copy(partOfSpeech = partsOfSpeech[position]) }
                    updateSaveButton()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
        customPartOfSpeechChecked(isCustomPartOfSpeech(card.partOfSpeech) == -1, card.partOfSpeech)
        editWordCardViewModel.antonyms = card.antonyms.toMutableList()
        editWordCardViewModel.synonyms = card.synonyms.toMutableList()
        editWordCardViewModel.example = card.examples.toMutableList()
        setupDynamicList("Examples", editWordCardViewModel.example, binding.examplesAddButton, binding.examplesListHeader, binding.examplesDynamicList)
        setupDynamicList("Synonyms", editWordCardViewModel.synonyms, binding.synonymsAddButton, binding.synonymsListHeader, binding.synonymsDynamicList)
        setupDynamicList("Antonyms", editWordCardViewModel.antonyms, binding.antonymsAddButton, binding.antonymsListHeader, binding.antonymsDynamicList)


    }

    fun customPartOfSpeechChecked(isChecked: Boolean, pos: String = editWordCardViewModel.wordCard.value!!.partOfSpeech) {
        binding.apply {
            if (isChecked) {
                spinner.visibility = View.GONE
                partOfSpeech.visibility = View.VISIBLE
                partOfSpeech.setText(pos)

            } else {
                spinner.visibility = View.VISIBLE
                partOfSpeech.visibility = View.GONE
                val spinnerPos = isCustomPartOfSpeech(pos)
                if (spinnerPos != -1)
                    spinner.setSelection(spinnerPos)
                editWordCardViewModel.updateCrime { card-> card.copy(partOfSpeech = partsOfSpeech[spinner.selectedItemPosition])}
            }
        }
        updateSaveButton()
    }

    fun isCustomPartOfSpeech(pos:String?):Int {
        return pos?.let {
            partsOfSpeech.indices.firstOrNull { i: Int ->
                partsOfSpeech[i].equals(
                    it
                )
            }
        } ?: -1
    }

    fun updateSaveButton(card: WordCard? = editWordCardViewModel.wordCard.value) {
        if (card == null) {
            binding.saveWordCardButton.isEnabled = false
            binding.deleteWordCardButton.isEnabled = false
            return
        }
        card!!.apply {
            val enabled =  !(folderID == null || definition.isNullOrEmpty() || partOfSpeech.isNullOrEmpty())
            binding.saveWordCardButton.isEnabled =enabled
            binding.deleteWordCardButton.isEnabled = enabled
        }
    }
    fun setupDynamicList(
        listName: String,
        arrayList: MutableList<String>,
        addButton: ImageButton,
        label: TextView,
        listView: RecyclerView
    ) {
        label.text = listName
        listView.layoutManager = LinearLayoutManager(context)
        val adapter: StringAdapter = StringAdapter(arrayList)
        listView.adapter = adapter

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add a new element")
        val alertDialog: AlertDialog = builder.create()
        val dialog_layout: View = layoutInflater.inflate(R.layout.dialog_new_string, null)
        val newString = dialog_layout.findViewById<EditText>(R.id.text)
        newString.hint = listName
        alertDialog.setView(dialog_layout)
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            "Create",
            { _, _ ->
                if (newString.text.isNotEmpty()) {
                    arrayList.add(newString.text.toString())
                    adapter.notifyItemInserted(arrayList.size - 1)
                }
            }
        )
        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            "Cancel", DialogInterface.OnClickListener { _, _ -> }
        )

        addButton.setOnTouchListener { _, _ ->
            alertDialog.show()
            true
        }
    }

    override fun onPause() {
        super.onPause()
        editWordCardViewModel.scrollPosition = binding.scrollView.scrollY
    }

    override fun onResume() {
        super.onResume()
        binding.scrollView.scrollY = editWordCardViewModel.scrollPosition
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}