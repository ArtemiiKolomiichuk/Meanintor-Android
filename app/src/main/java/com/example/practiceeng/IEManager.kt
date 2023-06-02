package com.example.practiceeng

import java.lang.StringBuilder
import java.util.UUID

/**
 * Import-Export Manager
 */
class IEManager {
    companion object{
        fun importWords(data: String, separator: String){
            val words = data.split(separator)
            //TODO: Move to bookmarked words
        }

        fun exportCards(cards: Array<WordCard>): String{
            val separator = "|||"
            val strBuilder = StringBuilder()
            strBuilder.append("[MeanintorWordCards]\n")
            for(card in cards){
                strBuilder.append(card.word.word).append(separator)
                strBuilder.append(card.partOfSpeech).append(separator)
                strBuilder.append(card.definition).append(separator)
                strBuilder.append(card.examples.joinToString(separator)).append(separator)
                strBuilder.append(card.synonyms.joinToString(separator)).append(separator)
                strBuilder.append(card.antonyms.joinToString(separator)).append(separator)
            }
            return strBuilder.toString()
        }

        /**
         * @param separators Array of separators that separate the [parts] of the card,
         * the last separator is the one that separates cards
         */
        fun importCards(data: String, separators: Array<String>, parts: Array<WordCardPart>, folderID: UUID){
            val cards : MutableList<WordCard> = mutableListOf()
            val cardsData = data.split(separators[separators.size - 1])
            for(cardData in cardsData){
                val card = WordCard("","", word = Word(""), folderID = folderID)
                var chunk = cardData
                var skipSeparators = 0
                for (i in 0 until parts.size - 1) {
                    val piece = chunk.substringBefore(separators[i + skipSeparators])
                    chunk = chunk.substringAfter(separators[i + skipSeparators])
                    when (parts[i]) {
                        WordCardPart.Word -> card.word = Word(piece)
                        WordCardPart.PartOfSpeech -> card.partOfSpeech = piece
                        WordCardPart.Definition -> card.definition = piece
                        WordCardPart.Examples -> {
                            card.examples = piece.split(separators[i + skipSeparators + 1]).toTypedArray()
                            skipSeparators++
                        }
                        WordCardPart.Synonyms -> {
                            card.synonyms = piece.split(separators[i + skipSeparators + 1]).toTypedArray()
                            skipSeparators++
                        }
                        WordCardPart.Antonyms -> {
                            card.antonyms = piece.split(separators[i + skipSeparators + 1]).toTypedArray()
                            skipSeparators++
                        }
                    }
                }
                when(parts[parts.size - 1]) {
                    WordCardPart.Word -> card.word = Word(chunk)
                    WordCardPart.PartOfSpeech -> card.partOfSpeech = chunk
                    WordCardPart.Definition -> card.definition = chunk
                    WordCardPart.Examples -> card.examples = chunk.split(separators[separators.size - 2]).toTypedArray()
                    WordCardPart.Synonyms -> card.synonyms = chunk.split(separators[separators.size - 2]).toTypedArray()
                    WordCardPart.Antonyms -> card.antonyms = chunk.split(separators[separators.size - 2]).toTypedArray()
                }
                cards.add(card)
            }
            //TODO: Move cards to specified folder
        }

        fun importCards(data: String, folderID: UUID){
            if(hasHeader(data)){
                importCards(data, arrayOf("|||"), arrayOf(WordCardPart.Word, WordCardPart.PartOfSpeech, WordCardPart.Definition, WordCardPart.Examples, WordCardPart.Synonyms, WordCardPart.Antonyms), folderID)
            }
        }

        private fun hasHeader(data: String): Boolean{
            return data.split("\n")[0] == "[MeanintorWordCards]"
        }
    }
}

enum class WordCardPart{
    Word,
    PartOfSpeech,
    Definition,
    Examples,
    Synonyms,
    Antonyms
}

