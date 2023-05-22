package com.example.practiceeng

/**
 * Amount of [TestType]s that have been completed
 * and the date of the last completion
 */
data class TrainingHistory
constructor(
    val types : Array<Pair<TestType, Int>> = arrayOf<Pair<TestType, Int>>(),
    val lastDate : String = "")
{

}

