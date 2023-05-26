package com.example.practiceeng

import java.util.Date

/**
 * Amount of [TestType]s that have been completed
 * and the date of the last completion
 */
data class TrainingHistory
constructor(
    var types : Array<Pair<TestType, Int>> = arrayOf<Pair<TestType, Int>>(),
    var lastDate : Date? = null)
{

}

