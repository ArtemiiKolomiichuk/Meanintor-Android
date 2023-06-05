package com.example.practiceeng

import androidx.annotation.Nullable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

/**
 * Card for learning a variant of [Word]
 */
@Entity(foreignKeys = arrayOf(ForeignKey(entity = Word::class, parentColumns = arrayOf("word"), childColumns = arrayOf("word"), onDelete = ForeignKey.RESTRICT),
    ForeignKey(entity = Folder::class, parentColumns = arrayOf("folderID"), childColumns = arrayOf("folderID"), onDelete = ForeignKey.CASCADE)))
data class WordCard(
    var partOfSpeech: String,
    var definition: String,
    var examples: Array<String> = arrayOf(),
    var synonyms: Array<String> = arrayOf(),
    var antonyms: Array<String> = arrayOf(),
    var word: Word,
    var folderID: UUID?,
    @PrimaryKey val cardID: UUID = UUID.randomUUID(),
    var trainingHistory: TrainingHistory = TrainingHistory(),
    var paused: Boolean = false,
    var mastery: Double = 0.0
)
{
    @Ignore
    fun word() : Word {
        return word
    }

    fun wordString() : String {
        return word().word
    }

    fun hasPhonetics(): Boolean {
        for(i in 0 until word().phonetics.size){
            if(word().phonetics[i].isNotBlank()){
                return true
            }
        }
        return false
    }

    fun audioLinks() : Array<String>{
        return word().audioLinks
    }

    fun hasExamples(): Boolean {
        return examples.isNotEmpty()
    }

    fun hasSynonyms(): Boolean {
        return synonyms.isNotEmpty()
    }

    /**
     * Returns examples of usage of the word with the word replaced with "______" if any
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
     * Returns an [amount] of definitions that *shouldn't* be synonymic to the word's definition
     *
     * @param cards list of cards to try to get not synonymic definitions from
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
            Utils.getGeneralDefinitionOptions(amount)
        }
    }

    /**
     * Returns an [amount] of words that *shouldn't* be synonymic to the word
     *
     * @param cards list of cards to try to get not synonymic words from
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
     * Returns an [amount] of words that *shouldn't* be antonymous to this word
     *
     * @param cards list of cards to try to get not antonymous words from
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
     * Returns whether the last training session was not long enough ago
     *
     * User can't train a word if returns true
     */
    fun trained() : Boolean {
        return -(trainingHistory.lastDate?.time?.minus(Date().time)?.div(1000)
            ?: 0) < UserSettings.settings().downgradeMargins[mastery.toInt()] * 0.75 * 3600
    }

    /**
     * Returns how many hours left to downgrade margin, may lower
     * [mastery] level of this card if it's too late
     *
     * Used to determine in which order [WordCard]s should be trained
     */
    fun retention() : Double {
        val seconds = trainingHistory.lastDate?.time?.minus(Date().time)?.div(1000) ?: 0
        val currentMargin = UserSettings.settings().downgradeMargins[mastery.toInt()] * 3600
        if (-seconds > currentMargin) {
            mastery = (mastery.toInt() - 1).coerceAtLeast(0).toDouble()
            adjustLastDate()
            return retention()
        }

        val remainingSeconds = currentMargin + seconds
        return if (remainingSeconds <= 0)
            5.0
        else
            remainingSeconds/3600.0
    }

    /**
     * Adjust last date in case of [mastery] level downgrade to prevent constant downgrades
     *
     * Sets last date to 3/4 of the downgrade margin of new mastery level
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
        val ordered = trainingOrder()
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
        val ordered = trainingHistory.types.sortedBy { it.second }
        var result = arrayOf<TestType>()
        for (i in ordered){
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
    private fun isAptForTraining(testType: TestType) : Boolean {
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



data class VisualWordCard(
    var partOfSpeech: String,
    var definition: String,
    var examples: Array<String> = arrayOf(),
    var synonyms: Array<String> = arrayOf(),
    var antonyms: Array<String> = arrayOf(),
    var word: Word,
    var folderID: UUID?,
    val cardID: UUID = UUID.randomUUID()
)