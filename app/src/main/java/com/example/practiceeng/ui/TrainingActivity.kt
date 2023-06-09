package com.example.practiceeng.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceeng.*
import com.example.practiceeng.TestType.*
import com.example.practiceeng.database.WordRepository
import com.example.practiceeng.databinding.ActivityTrainingBinding
import com.example.practiceeng.ui.fragments.TrainingSetupFragment
import com.example.practiceeng.ui.viewmodels.TrainingFragmentViewModel
import com.example.practiceeng.ui.viewmodels.TrainingFragmentViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import java.util.*


class TrainingActivity : AppCompatActivity() {



    val TAG = "TrainingActivity"
    private var _binding: ActivityTrainingBinding? = null
    val binding get() = _binding!!

    private val quizViewModel: TrainingFragmentViewModel by viewModels {
        TrainingFragmentViewModelFactory(intent.getIntExtra(TrainingSetupFragment.KEY_AMOUNT, 15), intent.getBooleanArrayExtra(TrainingSetupFragment.KEY_TESTTYPES)!!,
            intent.getSerializableExtra(TrainingSetupFragment.KEY_FOLDER_ID) as UUID
        )
    }
    private lateinit var testAnswersLayouts: Array<View>
//TODO("restrict popup dismissing with back button press")
    //TODO("fix exit")
    //TODO("popup when no questions are added")
    //TODO("add matching")
   private lateinit var bottomSheetDialog : BottomSheetDialog
    private lateinit var dialog_title   :TextView
   private lateinit var dialog_question:TextView
   private lateinit var dialog_answer  :TextView
   private lateinit var dialog_next    :Button
   private lateinit var dialog_correct :Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setContentView(R.layout.dialog_test_question_result)
          dialog_title = bottomSheetDialog.findViewById<TextView>(R.id.dialog_title)!!
          dialog_question = bottomSheetDialog.findViewById<TextView>(R.id.dialog_question)!!
          dialog_answer = bottomSheetDialog.findViewById<TextView>(R.id.dialog_answer)!!
          dialog_next = bottomSheetDialog.findViewById<Button>(R.id.next_question_button)!!
          dialog_correct = bottomSheetDialog.findViewById<Button>(R.id.correct_button)!!
        binding.apply {
            testAnswersLayouts = arrayOf(flashcardLayout, multipleChoiceLayout, trueFalseLayout, writingLayout)
        }
        if(quizViewModel.questionBank!=null)
            setupQuestionActivity()
        else
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
            binding.levelProgressBar.max = quizViewModel.size()
            binding.hintButton.setOnClickListener(object : OnClickListener {
                override fun onClick(v: View?) {
                    showHint()
                }
            })
            setupQuestion()
        }
    }

   fun setupQuestion() {
       binding.apply {
           currentQuestionNumber.text = (quizViewModel.index()+1).toString()
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
        binding.currentTask.visibility = View.VISIBLE
        when (quizViewModel.currentTestType()) {
            FlashCard -> {
                activateTestLayout(binding.flashcardLayout)
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
                    currentQuestion.visibility = View.GONE
                    smallerQuestion.visibility = View.GONE
                    playSoundView.visibility = View.GONE
                    currentTask.text = "Look and Remember"
                    continueButton.setOnClickListener { checkTestCorrectness(continueButton.text.toString()) }
                }
            }
            TrueFalse -> {
                activateTestLayout(binding.trueFalseLayout)
                binding.apply {
                    currentTask.text = "True or False?"
                    currentQuestion.text = quizViewModel.currentDisplayTexts()[0]
                    smallerQuestion.text = quizViewModel.currentDisplayTexts()[1]
                    currentQuestion.visibility = View.VISIBLE
                    smallerQuestion.visibility = View.VISIBLE
                    playSoundView.visibility = View.GONE
                    trueButton.setOnClickListener { checkTestCorrectness(trueButton.text.toString()) }
                    falseButton.setOnClickListener { checkTestCorrectness(falseButton.text.toString()) }
                }
            }
            MultipleChoiceWord, MultipleChoiceDefinition, Synonyms, Antonyms -> {
                activateTestLayout(binding.multipleChoiceLayout)
                binding.apply {
                    when (quizViewModel.currentTestType()) {
                        MultipleChoiceDefinition -> {
                            currentTask.text = "Pick a Matching Definition"
                            smallerQuestion.visibility = View.GONE
                            currentQuestion.visibility = View.VISIBLE
                            currentQuestion.text = quizViewModel.currentDisplayTexts()[0]
                        }
                        MultipleChoiceWord -> {
                            currentTask.text = "Pick a Matching Word"
                            smallerQuestion.visibility = View.VISIBLE
                            smallerQuestion.text = quizViewModel.currentDisplayTexts()[0]
                            currentQuestion.visibility = View.GONE
                        }
                        Synonyms -> {
                            currentTask.text = "Pick a Synonym"
                            smallerQuestion.visibility = View.GONE
                            currentQuestion.text = quizViewModel.currentDisplayTexts()[0]
                            currentQuestion.visibility = View.VISIBLE
                        }
                        Antonyms -> {
                            currentTask.text = "Pick an Antonym"
                            smallerQuestion.visibility = View.GONE
                            currentQuestion.visibility = View.VISIBLE
                            currentQuestion.text = quizViewModel.currentDisplayTexts()[0]
                        }
                        else -> {}
                    }
                    playSoundView.visibility = View.GONE
                    arrayOf(button1, button2, button3, button4).forEachIndexed { index, button ->
                        button.text = quizViewModel.currentOptions()[index]
                        button.setOnClickListener { checkTestCorrectness(button.text.toString()) }
                    }
                }
            }
            Match -> {}
            Writing, WritingListening -> {
                activateTestLayout(binding.writingLayout)
                binding.apply {
                    if (quizViewModel.currentTestType() == Writing) {
                        currentTask.text = "What Is This Word?"
                        smallerQuestion.visibility = View.VISIBLE
                        smallerQuestion.text = quizViewModel.currentDisplayTexts()[0]
                        playSoundView.visibility = View.GONE
                    } else if (quizViewModel.currentTestType() == WritingListening) {
                        currentTask.text = "What Do You Hear?"
                        smallerQuestion.visibility = View.GONE
                        playSoundView.visibility = View.VISIBLE
                        playSoundButton.setOnClickListener {
                            Utils.playAudio(
                                quizViewModel.currentDisplayTexts()[0],
                                this@TrainingActivity
                            )
                        }
                    }
                    currentQuestion.visibility = View.GONE
                    writingAnswerField.setText(quizViewModel.writingField)
                    writingAnswerField.doOnTextChanged { text, _, _, _ ->
                        if (text != null) {
                            quizViewModel.writingField = text.toString()
                            checkButton.isEnabled = text.isNotEmpty()
                        }
                    }
                    checkButton.setOnClickListener(object : OnClickListener {
                        override fun onClick(v: View?) {
                            checkTestCorrectness(quizViewModel.writingField)
                        }
                    })
                }
            }
            NONE -> throw IllegalArgumentException("\"NONE\" is not a valid test type")
        }
    }



    fun showHint() {
        if(quizViewModel.currentDisplayTextHint().isNotEmpty())
        Toast.makeText(this@TrainingActivity, quizViewModel.currentDisplayTextHint()[0], Toast.LENGTH_SHORT).show()
    }

    fun checkTestCorrectness(answer: String) {
        showDialog((answer in  quizViewModel.currentCorrectAnswers()))
    }

    fun showDialog(correct: Boolean, show: Boolean = true) {
        if (show) {
            when (correct) {
                true -> {
                    dialog_title.setText("Correct!")
                    if (!quizViewModel.hasNext()) {
                        dialog_next.setText("Return to menu")
                        binding.levelProgressBar.progress = binding.levelProgressBar.max
                    }

                    dialog_next.setOnClickListener (object: OnClickListener{
                        override fun onClick(v: View?) {
                            if (quizViewModel.moveToNext()) {
                                setupQuestion()
                                bottomSheetDialog.dismiss();
                            } else {
                                this@TrainingActivity.finish()
                            }
                        }
                    })
                    dialog_correct.visibility = View.GONE
                }
                false -> {
                    dialog_title.setText("Incorrect!")
                    dialog_next.setOnClickListener (object: OnClickListener {
                        override fun onClick(v: View?) {
                            quizViewModel.pushCurrentToEnd()
                            quizViewModel.moveToNext()
                            setupQuestion()
                            bottomSheetDialog.dismiss();
                        }
                    })
                    dialog_correct.visibility = View.VISIBLE
                    dialog_correct.setOnClickListener (object: OnClickListener{
                        override fun onClick(v: View?) {
                                bottomSheetDialog.dismiss()
                            showDialog(true)
                        }
                    })
                }
            }
            dialog_question.setText(quizViewModel.currentWordCards()[0].wordString())
            dialog_answer.setText(quizViewModel.currentWordCards()[0].definition)
            bottomSheetDialog.show()
        } else {
            if (quizViewModel.moveToNext() && correct) {
                setupQuestion()
                bottomSheetDialog.dismiss();
            }
        }
    }

    fun showIncorrectDialog(){

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
