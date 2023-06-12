package com.example.practiceeng.ui

import android.content.ClipData
import android.content.ClipDescription
import android.content.res.Resources
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnDragListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.children
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceeng.*
import com.example.practiceeng.TestType.*
import com.example.practiceeng.database.WordRepository
import com.example.practiceeng.databinding.ActivityTrainingBinding
import com.example.practiceeng.ui.fragments.TrainingSetupFragment
import com.example.practiceeng.ui.viewmodels.TrainingFragmentViewModel
import com.example.practiceeng.ui.viewmodels.TrainingFragmentViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import java.util.*


class TrainingActivity : AppCompatActivity() {


    val TAG = "TrainingActivity"
    private var _binding: ActivityTrainingBinding? = null
    val binding get() = _binding!!

    private val quizViewModel: TrainingFragmentViewModel by viewModels {
        TrainingFragmentViewModelFactory(
            intent.getIntExtra(TrainingSetupFragment.KEY_AMOUNT, 15),
            intent.getBooleanArrayExtra(TrainingSetupFragment.KEY_TESTTYPES)!!,
            intent.getSerializableExtra(TrainingSetupFragment.KEY_FOLDER_ID) as UUID?
        )
    }
    private lateinit var testAnswersLayouts: Array<View>

    //TODO("restrict popup dismissing with back button press")
    //TODO("fix exit")
    //TODO("popup when no questions are added")
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var dialog_title: TextView
    private lateinit var dialog_question: TextView
    private lateinit var dialog_answer: TextView
    private lateinit var dialog_next: Button
    private lateinit var dialog_correct: Button
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

        bottomSheetDialog.setCancelable(false)

        binding.apply {
            testAnswersLayouts = arrayOf(
                flashcardLayout,
                multipleChoiceLayout,
                trueFalseLayout,
                writingLayout,
                matchingLayout
            )
        }
        if (quizViewModel.questionBank != null)
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
                        WordRepository.get().getWordCardsFromFolder(quizViewModel.folderID!!)
                            .collect {
                                quizViewModel.updateQuestionBank(it)
                                setupQuestionActivity()
                            }
                    }
                }
            }
    }

    fun setupQuestionActivity() {
        binding.totalQuestionNumber.text = quizViewModel.size().toString()
        if (quizViewModel.moveToNext()) {
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
            currentQuestionNumber.text = (quizViewModel.index() + 1).toString()
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
            Match -> {
                binding.apply {
                    activateTestLayout(binding.matchingLayout)
                    val options: Array<TextView> =
                        arrayOf(option1, option2, option3, option4, option5)
                    val answers: Array<Pair<TextView, View>> = arrayOf(
                        Pair(variant1, cardView1),
                        Pair(variant2, cardView2),
                        Pair(variant3, cardView3),
                        Pair(variant4, cardView4),
                        Pair(variant5, cardView5)
                    )
                    quizViewModel.currentDisplayTexts().forEachIndexed { index, s ->
                        answers[index].apply {
                            first.setText(s)
                        }
                    }
                    quizViewModel.currentOptions().forEachIndexed { index, s ->
                        options[index].setText(s)
                    }

                    currentTask.text = "Match the Definitions"
                    currentQuestion.visibility = View.GONE
                    smallerQuestion.visibility = View.GONE
                    playSoundView.visibility = View.GONE
            matchingCheckButton.setOnClickListener(object:OnClickListener{
                override fun onClick(v: View?) {
                    showDialog(false, checkMatchingAnswers(answers))
                }
            })

                    addDragListeners(options, answers.map { it -> it.second }.toTypedArray())
                }

            }
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

    private fun checkMatchingAnswers(answers: Array<Pair<TextView, View>>): Array<String> {
        val wrongAnswers: MutableList<WordCard> = mutableListOf()
        val rightAnswers: MutableList<WordCard> = mutableListOf()
        var currentCard: WordCard? = null
        answers.forEach { pair ->
            currentCard = quizViewModel.currentWordCards().find { card -> card.definition == pair.first.text }
           val answersInField = (pair.second as CardView).children.toList()
            if(answersInField.isEmpty())
                wrongAnswers.add(currentCard!!)
            else {
                answersInField.first().let { text ->
                    val textAnswer = text as TextView
                    if (textAnswer.text != currentCard!!.wordString())
                        wrongAnswers.add(currentCard!!)
                    else
                        rightAnswers.add(currentCard!!)
                }
            }
/*card!!.wordString() == */
        }

        var incorrectText:String = ""
        var correctText:String = ""

            var first: Boolean = true
            for (a in wrongAnswers) {
                if (first)
                    first = false
                else
                    incorrectText += '\n'
                incorrectText += "${a.wordString()} - ${a.definition}"
            }
        first = true
        for (a in rightAnswers) {
            if (first)
                first = false
            else
                correctText += '\n'
            correctText += "${a.wordString()} - ${a.definition}"
        }
          return arrayOf(correctText, incorrectText)
    }

    class MatchingItemOnLongListener() : View.OnLongClickListener {
        override fun onLongClick(v: View?): Boolean {
            val string = (v as TextView).text.toString()
            val item = ClipData.Item(string)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData(string, mimeTypes, item)
            val dragShadowBuilder = View.DragShadowBuilder(v)
            v!!.startDragAndDrop(data, dragShadowBuilder, v, 0)
            return true
        }
    }

    private fun addDragListeners(dragViews: Array<TextView>, destinations: Array<View>) {
        val dragListener = OnDragListener { view, event ->
        val boxesLayoutCoords = intArrayOf(0, 0)
        view.getLocationInWindow(boxesLayoutCoords)
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    view.invalidate()
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    checkForScroll(event.y, boxesLayoutCoords[1])
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    view.invalidate()
                    true
                }
                DragEvent.ACTION_DROP -> {
                    val item = event.clipData.getItemAt(0)
                    val dragData = item.text
                 //   Toast.makeText(this@TrainingActivity, dragData, Toast.LENGTH_SHORT)
                    view.invalidate()
                    val v = event.localState as View
                    val owner = v.parent as ViewGroup
                    owner.removeView(v)
                    if (view in destinations) {
                        val cardViewDestination = destinations.first { it == view } as CardView
                        cardViewDestination.children.forEach { option ->
                            cardViewDestination.removeView(option)
                            binding.matchingOptions.addView(option)
                            binding.matchingOptions.invalidate()
                        }
                        val destination =view as CardView
                        destination.addView(v)
                    }
                    if(view == binding.matchingOptions) {
                        binding.matchingOptions.addView(v)
                    }
                    else{
                        v.visibility = View.VISIBLE
                        v.invalidate()
                        false
                    }
                    v.visibility = View.VISIBLE
                    true
                }

                DragEvent.ACTION_DRAG_EXITED -> {
                    view.invalidate()
                    true
                }
                else -> false
            }
        }
        for (dragView in dragViews) {
            dragViews.forEach { it -> it.setOnLongClickListener(MatchingItemOnLongListener()) }
        }
        for (destination in destinations + binding.matchingOptions) {
            destination.setOnDragListener(dragListener)
        }

        binding.trainingScrollView.setOnDragListener { _, dragEvent ->

            val boxesLayoutCoords = intArrayOf(0, 0)
            binding.trainingScrollView.getLocationInWindow(boxesLayoutCoords)

            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_LOCATION -> {
                    checkForScroll(dragEvent.y, boxesLayoutCoords[1])
                }
            }
            true
        }
    }
    val lowerLimForScroll = (Resources.getSystem().displayMetrics.heightPixels * 0.7).toInt()
    val upperLimForScroll = (Resources.getSystem().displayMetrics.heightPixels * 0.2).toInt()
    private fun checkForScroll(pointerY: Float, startOfScrollView: Int) {
        /* if the upper limit is passed, meaning a pixel height, scroll up */
        if ((pointerY + startOfScrollView) < upperLimForScroll) {
            binding.trainingScrollView.smoothScrollBy(0, -20)
        }
        /* if the lower limit is passed, meaning a pixel height, scroll down */
        else if (pointerY + startOfScrollView > lowerLimForScroll) {
            binding.trainingScrollView.smoothScrollBy(0, 15)
        }
    }

    fun showHint() {
        if (quizViewModel.currentDisplayTextHint().isNotEmpty())
            Toast.makeText(
                this@TrainingActivity,
                quizViewModel.currentDisplayTextHint()[0],
                Toast.LENGTH_SHORT
            ).show()
    }

    fun checkTestCorrectness(answer: String) {
        showDialog((answer in quizViewModel.currentCorrectAnswers()))
    }

    fun showDialog(correct: Boolean, matchAnswers:Array<String>? = null) {
        var isMatchingCorrect = false
        matchAnswers?.let {
            if(it[1].isEmpty())
                isMatchingCorrect = true
        }
            when (correct||isMatchingCorrect) {
                true -> {
                    dialog_title.setText("Correct!")
                    if (!quizViewModel.hasNext()) {
                        dialog_next.setText("Return to menu")
                        binding.levelProgressBar.progress = binding.levelProgressBar.max
                    }

                    dialog_next.setOnClickListener(object : OnClickListener {
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
                    dialog_next.setOnClickListener(object : OnClickListener {
                        override fun onClick(v: View?) {
                            quizViewModel.pushCurrentToEnd()
                            quizViewModel.moveToNext()
                            setupQuestion()
                            bottomSheetDialog.dismiss();
                        }
                    })
                    dialog_correct.visibility = View.VISIBLE
                    dialog_correct.setOnClickListener(object : OnClickListener {
                        override fun onClick(v: View?) {
                            bottomSheetDialog.dismiss()
                            showDialog(true, matchAnswers)
                        }
                    })
                }
            }

        if(matchAnswers==null) {
            dialog_question.setText(quizViewModel.currentWordCards()[0].wordString())
            dialog_answer.setText(quizViewModel.currentWordCards()[0].definition)
        } else {
            dialog_question.setText(matchAnswers[0])
            dialog_answer.setText(matchAnswers[1])
        }
            bottomSheetDialog.show()
    }

    fun activateTestLayout(layout: View) {
        if (layout != quizViewModel.currentLayout)
            testAnswersLayouts.forEach {
                if (layout == it)
                    it.visibility = View.VISIBLE
                else
                    it.visibility = View.GONE
            }
        quizViewModel.currentLayout = layout
        quizViewModel.writingField = ""
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}