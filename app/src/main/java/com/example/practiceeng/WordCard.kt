package com.example.practiceeng

import java.text.DateFormat
import java.util.Date

/**
 * Card for learning a variant of [Word]
 */
data class WordCard
constructor(
    var word: Word,
    var partOfSpeech: String,
    var definition: String,
    var examples: Array<String> = arrayOf<String>(),
    var synonyms: Array<String> = arrayOf<String>(),
    var antonyms: Array<String> = arrayOf<String>(),
    /*
        var typesArray : Array<Pair<TestType, Int>> = arrayOf<Pair<TestType, Int>>()
        var splitTypes = types.split("-")
        for (i in splitTypes.indices){
            typesArray += Pair(TestType.values()[i], splitTypes[i].toInt())
        }
    */
    var trainingHistory: TrainingHistory = TrainingHistory(),
    var bookmarked: Boolean = false,
    var paused : Boolean = false,
    var mastery: Double = 0.0,
    var folder : String = "")
{

    fun word() : String {
        return word.word
    }
    fun hasPhonetics(): Boolean {
        return word.phonetics.isNotEmpty() && (!word.phonetics[0].isNullOrBlank() || !word.phonetics[1].isNullOrBlank() || !word.phonetics[2].isNullOrBlank())
    }

    fun audioLink() : String?{
        return TODO()
    }

    fun phonetics(): Array<String> {
        return word.phonetics
    }
    fun hasExamples(): Boolean {
        return examples.isNotEmpty()
    }
    fun hasMultipleExamples(): Boolean {
        return examples.size > 1
    }
    fun hasSynonyms(): Boolean {
        return synonyms.isNotEmpty()
    }
    fun getHintExamples() : Array<String> {
        var hintExamples = arrayOf<String>()
        if(hasExamples()){
            for (example in examples){
                if (example.contains("<b>")) {
                    hintExamples += example.replace(Regex("<b>.*</b>"), "______")
                }else if (example.contains(word())) {
                    hintExamples += example.replace(word(), "______")
                }
            }
        }
        return hintExamples
    }

    fun getNotSynonymicDefinitions(amount : Int) : Array<String> {
        return TODO()
    }

    fun getNotSynonymicWords(amount : Int) : Array<String> {
        return TODO()
    }

    fun getNotAntonymousWords(amount : Int) : Array<String> {
        return TODO()
    }

    fun hasAntonyms(): Boolean {
        return antonyms.isNotEmpty()
    }
    fun isNew() : Boolean {
        return trainingHistory.lastDate == null
    }
    fun isMastered() : Boolean {
        return mastery >= UserSettings.settings().masteredMargin
    }

    /**
     * Returns a relative value of how well the word is known
     *
     * Used to determine in which order [WordCard]s should be trained
     *
     * **TODO: Test, written by ChatGPT**
     */
    fun retention() : Double {
        var seconds = trainingHistory.lastDate?.time?.minus(Date().time)?.div(1000) ?: 0

        val margins = listOf(0, 26, 65, 9 * 24, 17 * 24, 34 * 24, 100 * 24, 220 * 24)
        val retentionSteps = listOf(0.6, 0.8, 1.0, 1.2, 1.4, 1.6, 1.8, 2.0)

        val masteryLevel = mastery.toInt()
        val currentRetention = retentionSteps[masteryLevel]
        val currentMargin = margins[masteryLevel] * 3600 // Convert margin to seconds

        if (seconds > currentMargin) {
            val newMasteryLevel = (masteryLevel - 1).coerceAtLeast(0)
            return retentionSteps[newMasteryLevel]
        }

        val remainingSeconds = currentMargin - seconds
        val retentionDifference = (currentRetention - 1) * (remainingSeconds.toDouble() / currentMargin.toDouble())
        return currentRetention - retentionDifference
    }

    /**
     * Returns the most appropriate [TestType] for the next training
     *
     * *Does **not** check if the word is [paused]*
     */
    fun aptTraining() : TestType{
        var ordered = trainingOrder()
        for (type in ordered){
            if (isAptForTraining(type)){
                return type
            }
        }
        return TestType.NONE
    }

    /**
     * Returns the most appropriate [TestType] for the next training
     * from the specified [testTypes]
     *
     * *Does **not** check if the word is [paused]*
     */
    fun aptTraining(testTypes : Array<TestType>) : TestType{
        var ordered = trainingOrder()
        for (type in ordered){
            if (isAptForTraining(type) && testTypes.contains(type)){
                return type
            }
        }
        return TestType.ALL
    }

    /**
     * Returns a preferred order of [TestType]s for training
     */
    private fun trainingOrder() : Array<TestType>{
        var sorted = trainingHistory.types.sortedBy { it.second }
        var result = arrayOf<TestType>()
        for (i in sorted){
            result += i.first
        }
        return result
    }

    /**
     * Returns [TestType]s that the word can be trained in
     */
    fun aptForTrainings(testTypes: Array<TestType>) : Array<TestType>{
        var result = arrayOf<TestType>()
        for (type in testTypes){
            if (isAptForTraining(type)){
                result += type
            }
        }
        return result
    }

    /**
     * Returns true if the word can be trained in specified [TestType]
     *
     * *Does **not** check if the word is [paused]*
     * @param testType type of the test
     */
    fun isAptForTraining(testType: TestType) : Boolean {
        return when(testType){
            TestType.FlashCard -> true
            TestType.TrueFalse -> true
            TestType.MultipleChoiceWord -> true
            TestType.MultipleChoiceDefinition -> true
            TestType.Match -> true
            TestType.Synonyms -> hasSynonyms()
            TestType.Antonyms -> hasAntonyms()
            TestType.Writing -> true
            TestType.WritingListening -> hasPhonetics()
            TestType.ALL -> throw IllegalArgumentException("\"ALL\" is not a valid test type")
            TestType.NONE -> throw IllegalArgumentException("\"NONE\" is not a valid test type")
        }
    }
}

