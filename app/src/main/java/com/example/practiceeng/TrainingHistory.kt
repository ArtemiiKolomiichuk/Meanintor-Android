package com.example.practiceeng

import java.util.Date

/**
 * Amount of [TestType]s that have been completed
 * and the date of the last mastery level increase
 */
data class TrainingHistory
constructor(
    /**
     * [TestType]s and the amount of times they have been completed
     */
    var types : Array<Pair<TestType, Int>> = arrayOf(
        Pair(TestType.FlashCard, 0),
        Pair(TestType.TrueFalse, 0),
        Pair(TestType.MultipleChoiceWord, 0),
        Pair(TestType.MultipleChoiceDefinition, 0),
        Pair(TestType.Match, 0),
        Pair(TestType.Synonyms, 0),
        Pair(TestType.Antonyms, 0),
        Pair(TestType.Writing, 0),
        Pair(TestType.WritingListening, 0)
    ),
    var lastDate : Date? = null
)