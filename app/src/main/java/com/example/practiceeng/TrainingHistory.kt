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
    var types : Array<Pair<TestType, Int>> = arrayOf(),
    var lastDate : Date? = null)

