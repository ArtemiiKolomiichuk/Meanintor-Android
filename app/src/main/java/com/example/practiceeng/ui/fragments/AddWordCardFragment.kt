package com.example.practiceeng.ui.fragments

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceeng.R
import com.example.practiceeng.databinding.FragmentAddWordCardBinding
import com.example.practiceeng.ui.adapters.StringAdapter
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

    private lateinit var partsOfSpeech: Array<String>
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
            addWordCardViewModel.name?.let { word.setText(addWordCardViewModel.name) }

            addWordCardViewModel.def?.let { definition.setText(addWordCardViewModel.def) }

            isPhrase.setOnCheckedChangeListener { _, isChecked ->
                customPartOfSpeechChecked(isChecked)
                updateSaveButton()
            }
            partOfSpeech.doOnTextChanged { text, _, _, _ ->
                addWordCardViewModel.pos = text.toString()
                updateSaveButton()
            }
            definition.doOnTextChanged { text, _, _, _ ->
                addWordCardViewModel.def = text.toString()
                updateSaveButton()
            }
            saveWordCardButton.setOnClickListener {
                addWordCardViewModel.addWordCard()
                findNavController().navigateUp()
            }
            chooseFolderButton.setOnClickListener {
                findNavController().navigate(
                    AddWordCardFragmentDirections.chooseFolder(
                        addWordCardViewModel.folder
                    )
                )
            }
            setupDynamicList("Examples", addWordCardViewModel.example, binding.examplesAddButton, binding.examplesListHeader, binding.examplesDynamicList)
            setupDynamicList("Synonyms", addWordCardViewModel.synonyms, binding.synonymsAddButton, binding.synonymsListHeader, binding.synonymsDynamicList)
            setupDynamicList("Antonyms", addWordCardViewModel.antonyms, binding.antonymsAddButton, binding.antonymsListHeader, binding.antonymsDynamicList)

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    addWordCardViewModel.pos = partsOfSpeech[position]
                    updateSaveButton()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
            customPartOfSpeechChecked(isCustomPartOfSpeech(addWordCardViewModel.pos) == -1)
        }

        setFragmentResultListener(
            ChooseFolderFragment.REQUEST_KEY_FOLDER
        ) { requestKey, bundle ->
            addWordCardViewModel.folder =
                bundle.getSerializable(ChooseFolderFragment.BUNDLE_KEY_FOLDER) as UUID?
            updateSaveButton()
        }
    }

    fun customPartOfSpeechChecked(isChecked: Boolean) {
        binding.apply {
            if (isChecked) {
                spinner.visibility = View.GONE
                partOfSpeech.visibility = View.VISIBLE
                isPhrase.isChecked = true
                partOfSpeech.setText(addWordCardViewModel.pos)

            } else {
                spinner.visibility = View.VISIBLE
                partOfSpeech.visibility = View.GONE
                isPhrase.isChecked = false
                val spinnerPos = isCustomPartOfSpeech(addWordCardViewModel.pos)
                if (spinnerPos != -1)
                    spinner.setSelection(spinnerPos)
                addWordCardViewModel.pos = partsOfSpeech[spinner.selectedItemPosition]
            }
        }
        updateSaveButton()
    }

    fun isCustomPartOfSpeech(pos: String?): Int {
        return pos?.let {
            partsOfSpeech.indices.firstOrNull { i: Int ->
                partsOfSpeech[i].equals(
                    it
                )
            }
        } ?: -1
    }

    fun updateSaveButton() {
        addWordCardViewModel.apply {
            binding.saveWordCardButton.isEnabled =
                !(folder == null || def.isNullOrEmpty() || pos.isNullOrEmpty())
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
        val adapter: StringAdapter = StringAdapter(arrayList) { oldValue:String, position: Int, adapter: StringAdapter ->
            val changeElementDialog = changeElementDialog(oldValue, arrayList, position, adapter)
            changeElementDialog.show()
        }
        listView.adapter = adapter

        val addElementDialog = addElementDialog(listName, arrayList, adapter)

        addButton.setOnTouchListener { _, _ ->
            addElementDialog.show()
            true
        }
    }

    private fun changeElementDialog(oldValue:String, arrayList: MutableList<String>, position:Int, adapter: StringAdapter): AlertDialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Change an element")
        val alertDialog: AlertDialog = builder.create()
        val dialog_layout: View = layoutInflater.inflate(R.layout.dialog_new_string, null)
        val newString = dialog_layout.findViewById<EditText>(R.id.text)
        newString.hint = oldValue
        newString.setText(oldValue)
        alertDialog.setView(dialog_layout)
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            "Change",
            { _, _ ->
                if (newString.text.isNotEmpty()) {
                    arrayList[position]=newString.text.toString()
                    adapter.notifyItemChanged(position)
                }
            }
        )
        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            "Cancel", DialogInterface.OnClickListener { _, _ -> }
        )
        alertDialog.setOnShowListener(object: DialogInterface.OnShowListener {
            override fun onShow(dialog: DialogInterface?) {
                val saveButton: Button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                saveButton.setEnabled(false)
                newString.doOnTextChanged { text, _, _, _ -> saveButton.setEnabled( !(text.toString().isEmpty()||text.toString().equals(oldValue)))  }
            }
        });
        return alertDialog

    }

    private fun addElementDialog(listName:String, arrayList: MutableList<String>, adapter: StringAdapter) : AlertDialog {
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
        alertDialog.setOnShowListener(object: DialogInterface.OnShowListener {
            override fun onShow(dialog: DialogInterface?) {
                newString.setText("")
                val saveButton: Button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                newString.doOnTextChanged { text, _, _, _ -> saveButton.setEnabled( !(text.toString().isEmpty()))  }
            }
        });
        return alertDialog

    }

    override fun onPause() {
        super.onPause()
        addWordCardViewModel.scrollPosition = binding.scrollView.scrollY
    }

    override fun onResume() {
        super.onResume()
        binding.scrollView.scrollY = addWordCardViewModel.scrollPosition
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}