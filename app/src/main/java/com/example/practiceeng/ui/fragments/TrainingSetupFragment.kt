package com.example.practiceeng.ui.fragments

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
import com.example.practiceeng.ui.viewmodels.TrainingSetupViewModel
import com.example.practiceeng.ui.viewmodels.TrainingSetupViewModelFactory

class TrainingSetupFragment : Fragment() {

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
        }
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

        binding.continueButton.setOnTouchListener { v, event ->
                TestType.all().forEachIndexed { index: Int, it: TestType ->
                    if (typesMap.getValue(it).isChecked || binding.switch1.isChecked)
                        viewModel.typesBooleanArray[index] = true
                }
            findNavController().navigate(TrainingSetupFragmentDirections.startTraining(viewModel.maxWords, viewModel.typesBooleanArray, viewModel.folderId))
                true
            }
    }

    fun selectAllOptions(on: Boolean) {
        TestType.all().forEach { typesMap.getValue(it).isChecked = on }
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