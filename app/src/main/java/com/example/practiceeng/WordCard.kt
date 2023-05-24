package com.example.practiceeng
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
    fun hasAntonyms(): Boolean {
        return antonyms.isNotEmpty()
    }
    fun isNew() : Boolean {
        return trainingHistory.lastDate == ""
    }
    fun isMastered() : Boolean {
        return mastery >= UserSettings.settings().masteredMargin
    }

    /**
     * Returns a relative value of how well the word is known
     *
     * Used to determine in which order [WordCard]s should be trained
     */
    fun comprehension() : Double {
        TODO()
        //add dates difference
        when(mastery){
            1.0 -> return 1.0
            2.0 -> return 5.0
        }
        return mastery/10
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
        return TestType.FlashCard
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

