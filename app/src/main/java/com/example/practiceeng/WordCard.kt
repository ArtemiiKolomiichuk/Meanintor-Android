package com.example.practiceeng

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

/**
 * Card for learning a variant of [Word]
 */
@Entity
data class WordCard(
    var partOfSpeech: String,
    var definition: String,
    var examples: Array<String> = arrayOf<String>(),
    var synonyms: Array<String> = arrayOf<String>(),
    var antonyms: Array<String> = arrayOf<String>(),
    var trainingHistory: TrainingHistory = TrainingHistory(),
    var paused : Boolean = false,
    var mastery: Double = 0.0,
    var wordID: UUID,
    var folderID : UUID,
    @PrimaryKey val cardID : UUID = UUID.randomUUID())
{
    fun word() : Word {
        return TODO()//#DataBase
    }

    fun setWord(word: Word) {
        wordID = word.wordID
    }

    fun wordString() : String {
        return word().word
    }
    fun hasPhonetics(): Boolean {
        return word().phonetics.isNotEmpty() && (!word().phonetics[0].isNullOrBlank() || !word().phonetics[1].isNullOrBlank() || !word().phonetics[2].isNullOrBlank())
    }

    fun audioLinks() : Array<String>{
        return word().audioLinks
    }

    fun phonetics(): Array<String> {
        return word().phonetics
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

    /**
     * Returns examples of usage of the word with the word replaced with "______"
     */
    fun getHintExamples() : Array<String> {
        var hintExamples = arrayOf<String>()
        if(hasExamples()){
            for (example in examples){
                if (example.contains("<b>")) {
                    hintExamples += example.replace(Regex("<b>.*</b>"), "______")
                }else if (example.contains(wordString())) {
                    hintExamples += example.replace(wordString(), "______")
                }
            }
        }
        return hintExamples
    }

    /**
     * Returns definitions that *shouldn't* be synonymic to the word's definition
     */
    fun getNotSynonymicDefinitions(amount : Int, cards: MutableList<WordCard>) : Array<String> {
        var notSynonymicDefinitions = arrayOf<String>()
        var notSynonymicCards = cards.filter { it.partOfSpeech == partOfSpeech && !synonyms.contains(it.wordString()) && !it.synonyms.contains(wordString()) }
        return if (notSynonymicCards.size > amount*2) {
            notSynonymicCards = notSynonymicCards.shuffled().take(amount)
            for (card in notSynonymicCards) {
                notSynonymicDefinitions += card.definition
            }
            notSynonymicDefinitions
        }else{
            Utils.getGeneralDefinitionOptions(amount, synonyms)
        }
    }

    /**
     * Returns words that *shouldn't* be synonymic to the word
     */
    fun getNotSynonymicWords(amount : Int, cards: MutableList<WordCard>) : Array<String> {
        var notSynonymicWords = arrayOf<String>()
        var notSynonymicCards = cards.filter { it.partOfSpeech == partOfSpeech && !synonyms.contains(it.wordString()) && !it.synonyms.contains(wordString())}
        return if (notSynonymicCards.size > amount*2) {
            notSynonymicCards = notSynonymicCards.shuffled().take(amount)
            for (card in notSynonymicCards) {
                notSynonymicWords += card.wordString()
            }
            notSynonymicWords
        }else{
            Utils.getGeneralWordOptions(amount, synonyms, partOfSpeech)
        }
    }

    /**
     * Returns words that *shouldn't* be antonymous to the word
     */
    fun getNotAntonymousWords(amount : Int, cards: MutableList<WordCard>) : Array<String> {
        var notAntonymousWords = arrayOf<String>()
        var notAntonymousCards = cards.filter { it.partOfSpeech == partOfSpeech && !antonyms.contains(it.wordString()) && !it.antonyms.contains(wordString()) }
        return if (notAntonymousCards.size > amount*2) {
            notAntonymousCards = notAntonymousCards.shuffled().take(amount)
            for (card in notAntonymousCards) {
                notAntonymousWords += card.wordString()
            }
            notAntonymousWords
        }else{
            Utils.getGeneralWordOptions(amount, antonyms, partOfSpeech)
        }
    }

    fun hasAntonyms(): Boolean {
        return antonyms.isNotEmpty()
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

        val retentionSteps = listOf(0.6, 0.8, 1.0, 1.2, 1.4, 1.6, 1.8, 2.0)

        val masteryLevel = mastery.toInt()
        val currentRetention = retentionSteps[masteryLevel]
        val currentMargin = UserSettings.settings().downgradeMargins[masteryLevel] * 3600

        if (seconds > currentMargin) {
            mastery = (masteryLevel - 1).coerceAtLeast(0).toDouble()
            adjustLastDate()
            return retentionSteps[mastery.toInt()]
        }

        val remainingSeconds = currentMargin - seconds
        val retentionDifference = (currentRetention - 1) * (remainingSeconds.toDouble() / currentMargin.toDouble())
        return currentRetention - retentionDifference
    }

    /**
     * Adjust last date in case of mastery downgrade to prevent constant downgrades
     *
     * Sets last date to 3/4 of the margin of new mastery level
     */
    fun adjustLastDate(){
        trainingHistory.lastDate = Date(Date().time - (UserSettings.settings().downgradeMargins[mastery.toInt()] * 3600 * 3 * 250))
    }

    /**
     * Returns the most appropriate [TestType]s for the next training
     * from the specified [testTypes]
     *
     * *Does **not** check if the word is [paused]*
     */
    fun aptTrainings(testTypes : Array<TestType>) : Array<TestType>{
        val order = trainingOrder()
        var result = arrayOf<TestType>()
        for (type in order){
            if (isAptForTraining(type) && testTypes.contains(type)){
                result += type
            }
        }
        return result
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
        return TestType.NONE
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
            TestType.WritingListening -> audioLinks().isNotEmpty()
            TestType.NONE -> throw IllegalArgumentException("\"NONE\" is not a valid test type")
        }
    }
}

