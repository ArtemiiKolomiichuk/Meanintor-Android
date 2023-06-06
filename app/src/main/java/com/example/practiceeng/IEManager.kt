package com.example.practiceeng

import com.example.practiceeng.database.WordRepository
import java.lang.StringBuilder
import java.util.UUID

/**
 * Import-Export Manager
 */
class IEManager {
    companion object{
        fun importWords(data: String, separator: String){
            val words = data.split(separator)
            for(word in words){
                WordRepository.get().addWord(Word(word, bookmarked = true))
            }
        }

        fun exportCards(cards: Array<WordCard>): String{
            val separator = "|||"
            val separator2 = "@@@"
            val separator3 = "\n#\n"
            val strBuilder = StringBuilder()
            strBuilder.append("[MeanintorWordCards]\n")
            for(card in cards){
                strBuilder.append(card.word.word).append(separator)
                strBuilder.append(card.partOfSpeech).append(separator)
                strBuilder.append(card.definition).append(separator)
                strBuilder.append(card.examples.joinToString(separator2)).append(separator)
                strBuilder.append(card.synonyms.joinToString(separator2)).append(separator)
                strBuilder.append(card.antonyms.joinToString(separator2)).append(separator3)
            }
            return strBuilder.toString()
        }

        /**
         * @param separators Array of separators that separate the [parts] of the card,
         * the last separator is the one that separates cards
         */
        private fun extractCards(data: String, separators: Array<String>, parts: Array<WordCardPart>) : MutableList<WordCard>{
            val cards : MutableList<WordCard> = mutableListOf()
            val cardsData = data.split(separators[separators.size - 1])
            for(cardData in cardsData){
                val card = WordCard("","", word = Word(""), folderID = null)
                var chunk = cardData
                var skipSeparators = 0
                for (i in 0 until parts.size - 1) {
                    when (parts[i]) {
                        WordCardPart.Word -> {
                            val piece = chunk.substringBefore(separators[i + skipSeparators])
                            chunk = chunk.substringAfter(separators[i + skipSeparators])
                            card.word = Word(piece)
                        }
                        WordCardPart.PartOfSpeech -> {
                            val piece = chunk.substringBefore(separators[i + skipSeparators])
                            chunk = chunk.substringAfter(separators[i + skipSeparators])
                            card.partOfSpeech = piece
                        }
                        WordCardPart.Definition -> {
                            val piece = chunk.substringBefore(separators[i + skipSeparators])
                            chunk = chunk.substringAfter(separators[i + skipSeparators])
                            card.definition = piece
                        }


                        WordCardPart.Examples -> {
                            val piece = chunk.substringBefore(separators[i + skipSeparators + 1])
                            chunk = chunk.substringAfter(separators[i + skipSeparators + 1])

                            card.examples = piece.split(separators[i + skipSeparators]).toTypedArray()
                            skipSeparators++
                        }
                        WordCardPart.Synonyms -> {
                            val piece = chunk.substringBefore(separators[i + skipSeparators + 1])
                            chunk = chunk.substringAfter(separators[i + skipSeparators + 1])

                            card.synonyms = piece.split(separators[i + skipSeparators]).toTypedArray()
                            skipSeparators++
                        }
                        WordCardPart.Antonyms -> {
                            val piece = chunk.substringBefore(separators[i + skipSeparators + 1])
                            chunk = chunk.substringAfter(separators[i + skipSeparators + 1])

                            card.antonyms = piece.split(separators[i + skipSeparators]).toTypedArray()
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
                if(card.word.word.isNotBlank()){
                    cards.add(card)
                }
            }
            return cards
        }

        fun importCards(data: String, folderID: UUID){
            if(!hasHeader(data)){
                return
            }
            val cards = extractCards(data.substringAfter("[MeanintorWordCards]\n"), arrayOf("|||","|||","|||","@@@","|||","@@@","|||","@@@","\n#\n"
            ), arrayOf(WordCardPart.Word, WordCardPart.PartOfSpeech, WordCardPart.Definition, WordCardPart.Examples, WordCardPart.Synonyms, WordCardPart.Antonyms))
            importCards(cards.toTypedArray(), folderID)
        }

        fun importCards(data: String, separators: Array<String>, parts: Array<WordCardPart>, folderID: UUID) {
            importCards(extractCards(data, separators, parts).toTypedArray(), folderID)
        }

        fun importCards(cards: Array<WordCard>, folderID: UUID){
            for (card in cards){
                card.folderID = folderID
                WordRepository.get().addWordCard(card)
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

