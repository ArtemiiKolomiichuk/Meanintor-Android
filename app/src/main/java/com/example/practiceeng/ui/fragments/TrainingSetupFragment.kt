package com.example.practiceeng.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.practiceeng.TestType
import com.example.practiceeng.Utils
import com.example.practiceeng.databinding.FragmentTrainingSetupBinding
import com.example.practiceeng.ui.TrainingActivity
import com.example.practiceeng.ui.fragments.TrainingSetupFragmentDirections.startTraining
import com.example.practiceeng.ui.viewmodels.TrainingSetupViewModel
import com.example.practiceeng.ui.viewmodels.TrainingSetupViewModelFactory

class TrainingSetupFragment : Fragment() {

    companion object
    {
        val KEY_AMOUNT="KEY_AMOUNT"
        val KEY_TESTTYPES="KEY_TESTTYPES"
        val KEY_FOLDER_ID = "KEY_FOLDER_ID"
    }

    private val args: TrainingSetupFragmentArgs by navArgs()
    private val viewModel: TrainingSetupViewModel by viewModels {
        TrainingSetupViewModelFactory(args.folderID)
    }
    var _binding: FragmentTrainingSetupBinding? = null
    val binding get() = _binding!!
    lateinit var typesMap: Map<TestType, CheckBox>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTrainingSetupBinding.inflate(inflater, container, false)
        typesMap = mapOf(
            Pair(TestType.FlashCard, binding.flashcards),
            Pair(TestType.TrueFalse, binding.trueFalse),
            Pair(TestType.MultipleChoiceWord, binding.multipleChoiceWord),
            Pair(TestType.MultipleChoiceDefinition, binding.multpleChoiceDefinition),
            Pair(TestType.Match, binding.matching),
            Pair(TestType.Synonyms, binding.synonyms),
            Pair(TestType.Antonyms, binding.antonyms),
            Pair(TestType.Writing, binding.writing),
            Pair(TestType.WritingListening, binding.writingListening)
        )
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.maxWords?.let {
            binding.maxWordsSeekbar.progress = it - 1
            binding.maxWordsCount.text = it.toString()
        }
        binding.selectAllTrainingButton.setOnTouchListener { v, event -> selectAllOptions(true)
            true}
        binding.deselect.setOnTouchListener { v, event -> selectAllOptions(false)
            true}
        binding.switch1.setOnCheckedChangeListener { buttonView, isChecked ->
            hideAllOptions(isChecked)
            updateContinueButton()
        }
        TestType.all().forEach { typesMap.getValue(it).setOnCheckedChangeListener {_, isChecked ->  updateContinueButton() }}
        binding.switch1.isChecked = false
        selectAllOptions(false)
        binding.maxWordsSeekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.maxWords = progress + 1
                binding.maxWordsCount.text = viewModel.maxWords.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.continueButton.setOnClickListener(object :OnClickListener{
            override fun onClick(v: View?) {
                findNavController().navigateUp()
                startTrainingActivity()
            }
        })
        updateContinueButton()
    }

    private fun startTrainingActivity() {
        TestType.all().forEachIndexed { index: Int, it: TestType ->
            if (typesMap.getValue(it).isChecked || binding.switch1.isChecked)
                viewModel.typesBooleanArray[index] = true
        }

        val intent = Intent(requireContext(), TrainingActivity::class.java)
        intent.putExtra(KEY_AMOUNT, viewModel.maxWords)
        intent.putExtra(KEY_TESTTYPES, viewModel.typesBooleanArray)
        intent.putExtra(KEY_FOLDER_ID, viewModel.folderId)
        startActivity(intent)
    }

    //TODO("disable button when no types are selected")

    fun selectAllOptions(on: Boolean) {
        TestType.all().forEach { typesMap.getValue(it).isChecked = on }
    }

    fun isEveryCheckboxUnchecked():Boolean{
        TestType.all().forEach {
          if(typesMap.getValue(it).isChecked)
              return false
        }
        return true
    }

    fun updateContinueButton(){
        binding.continueButton.isEnabled = (binding.switch1.isChecked || !isEveryCheckboxUnchecked())
    }


    fun hideAllOptions(on: Boolean) {
        if (on) {
            binding.selectAllTrainingButton.visibility = View.GONE
            binding.deselect.visibility = View.GONE
            TestType.all().forEach {
                typesMap.getValue(it).visibility = View.GONE
            }
        } else {
            TestType.all().forEach { typesMap.getValue(it).visibility = View.VISIBLE }
            binding.selectAllTrainingButton.visibility = View.VISIBLE
            binding.deselect.visibility = View.VISIBLE
        }
    }
}