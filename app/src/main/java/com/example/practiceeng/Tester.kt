package com.example.practiceeng
import android.util.Log
import java.net.URL


    fun main() {
        /*
        APIHandler.print("hello", DictionaryAPI.FreeDictionaryAPI)
        println()
        //APIHandler.print("hello", DictionaryAPI.WordsAPI)
        var w : WordCard = WordCard(Word("hello"), "noun","greeting")
        w.trainingHistory = TrainingHistory(arrayOf(Pair(TestType.MultipleChoiceWord, 14), Pair(TestType.FlashCard, 6), Pair(TestType.TrueFalse, 2)))
        println(w.aptTraining())
        println(w.definition)
        w.definition = "a greeting"
        println(w.definition)
        println()
        println(w)
        */

        val cards = APIHandler.getCards("hello", DictionaryAPI.FreeDictionaryAPI)
        for (card in cards) {
            println(card.toString())
        }
    }

