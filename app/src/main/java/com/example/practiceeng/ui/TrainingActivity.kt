package com.example.practiceeng.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceeng.Question
import com.example.practiceeng.QuestionManager
import com.example.practiceeng.TestType
import com.example.practiceeng.TestType.*
import com.example.practiceeng.database.WordRepository
import com.example.practiceeng.databinding.ActivityTrainingBinding
import com.example.practiceeng.ui.viewmodels.TrainingFragmentViewModel
import com.example.practiceeng.ui.viewmodels.TrainingFragmentViewModelFactory
import kotlinx.coroutines.launch


class TrainingActivity : AppCompatActivity() {
    val TAG = "TrainingActivity"
    private var _binding: ActivityTrainingBinding? = null
    val binding get() = _binding!!
    private val args: TrainingActivityArgs by navArgs()
    private val quizViewModel: TrainingFragmentViewModel by viewModels {
        TrainingFragmentViewModelFactory(args.amount, args.testTypes, args.folders)
    }
    private lateinit var testAnswersLayouts: Array<View>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            testAnswersLayouts = arrayOf(flashcard, multipleChoice, trueFalse)
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                if (quizViewModel.folderID == null) {
                    WordRepository.get().getWordCards().collect {
                        quizViewModel.updateQuestionBank(it)
                        setupQuestionActivity()
                    }
                } else {
                    WordRepository.get().getWordCardsFromFolder(quizViewModel.folderID!!).collect {
                        quizViewModel.updateQuestionBank(it)
                        setupQuestionActivity()
                    }
                }
            }
        }
    }

    fun setupQuestionActivity() {
        binding.totalQuestionNumber.text = quizViewModel.size().toString()
        if (quizViewModel.hasNext()) {
            Log.d(TAG, quizViewModel.questionBank.joinToString(", "))
            Log.d(TAG, quizViewModel.currentIndex.toString())
            binding.levelProgressBar.max = quizViewModel.size()
            setupQuestion()
        }

    }

   fun setupQuestion() {
       binding.apply {
           currentQuestionNumber.text = (quizViewModel.index()+1).toString()
           quizViewModel.currentDisplayTexts().let {
               if (it.isNotEmpty()) {
                   currentQuestion.text = it[0]
                   currentQuestion.visibility = View.VISIBLE
               } else {
                   currentQuestion.visibility = View.GONE
               }
           }
           quizViewModel.currentDisplayTextHint().let {
               if (it.isNotEmpty()) {
                   hintButton.visibility = View.VISIBLE
               } else {
                   hintButton.visibility = View.GONE
               }
           }
           levelProgressBar.progress = quizViewModel.index()
           showTest()
       }
   }

    fun showTest() {
        when (quizViewModel.currentTestType()) {
            FlashCard -> {
                activateTestLayout(binding.flashcard)
                val card = quizViewModel.currentWordCards()[0]
                binding.apply {
                    cardItem.apply {
                        word.setText(card.word.word)
                        partOfSpeech.setText(card.partOfSpeech)
                        definitionString.setText(card.definition)
                        if (card.examples.isNotEmpty()) {
                            example.setText(card.examples.joinToString("\n"))
                        } else
                            example.visibility = RecyclerView.GONE
                        if (card.synonyms.isNotEmpty()) {
                            synonyms.setText(card.synonyms.joinToString())
                        } else {
                            synonymsDivider.visibility = RecyclerView.GONE
                            synonymsTitle.visibility = RecyclerView.GONE
                            synonyms.visibility = RecyclerView.GONE
                        }
                        if (card.antonyms.isNotEmpty()) {
                            antonyms.setText(card.antonyms.joinToString())
                        } else {
                            antonymsDivider.visibility = RecyclerView.GONE
                            antonymsTitle.visibility = RecyclerView.GONE
                            antonyms.visibility = RecyclerView.GONE
                        }
                    }
                    continueButton.setOnClickListener { showCorrectDialog() }
                }
            }
            TrueFalse -> {
                activateTestLayout(binding.trueFalse)
            }
            MultipleChoiceWord, MultipleChoiceDefinition -> {
                activateTestLayout(binding.multipleChoice)
            }
            Match -> {}
            Synonyms, Antonyms -> {}
            Writing -> {}
            WritingListening -> {}
            NONE -> throw IllegalArgumentException("\"NONE\" is not a valid test type")


        }
    }

    fun showCorrectDialog(){
        Toast.makeText(this@TrainingActivity, "Correct", Toast.LENGTH_SHORT).show()
        if(quizViewModel.moveToNext())
        setupQuestion()
        else showFinalDialog()
    }

    private fun showFinalDialog() {
        binding.levelProgressBar.progress = binding.levelProgressBar.max
    }

    fun showIncorrectDialog(){
        Toast.makeText(this@TrainingActivity, "Incorrect", Toast.LENGTH_SHORT).show()
        if(quizViewModel.moveToNext())
            setupQuestion()
        else showFinalDialog()
    }
    fun activateTestLayout(layout: View) {
        testAnswersLayouts.forEach {
            if (layout == it)
                it.visibility = View.VISIBLE
            else
                it.visibility = View.GONE
        }
    }
}
