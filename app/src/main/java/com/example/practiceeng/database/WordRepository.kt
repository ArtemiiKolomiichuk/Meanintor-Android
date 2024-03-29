package com.example.practiceeng.database

import android.content.Context
import androidx.room.Room
import com.example.practiceeng.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*

private const val DATABASE_NAME = "word-database"
class WordRepository private constructor(context: Context,
                                         private val coroutineScope: CoroutineScope = GlobalScope
) {
    private val database: WordDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            WordDatabase::class.java,
            DATABASE_NAME
        )
        // .createFromAsset(DATABASE_NAME)
        .build()

    fun updateWord(word: Word) {
        coroutineScope.launch {
            database.wordDao().updateWord(word)
        }
    }

    fun addWord(word: Word) {
        coroutineScope.launch {
            database.wordDao().addWord(word)
        }
    }

    fun updateFolder(folder: Folder) {
        coroutineScope.launch {
            database.wordDao().updateFolder(folder)
        }
    }

    fun addFolder(folder: Folder) {
        coroutineScope.launch {
            database.wordDao().addFolder(folder)
        }
    }

    fun updateWordCard(wordCard: WordCard) {
        coroutineScope.launch {
            database.wordDao().updateWordCard(wordCard)
        }
    }

    fun addWordCard(wordCard: WordCard) {
        coroutineScope.launch {
            database.wordDao().addWordCard(wordCard)
        }
    }

    fun getWords(): Flow<List<Word>> = database.wordDao().getWords()
    fun getWord(name: String): Word = database.wordDao().getWord(name)
    fun getFolders(): Flow<List<Folder>> = database.wordDao().getFolders()
    suspend fun getFolder(folderID: UUID): Folder = database.wordDao().getFolder(folderID)
    fun getVisualWordCardsFromFolder(folderID: UUID): Flow<List<VisualWordCard>> =
        database.wordDao().getVisualWordCardsFromFolder(folderID)

    fun getWordCardsFromFolder(folderID: UUID): Flow<List<WordCard>> =
        database.wordDao().getWordCardsFromFolder(folderID)

    suspend fun getCountedFolders(): Flow<List<CountedFolder>> =
        database.wordDao().getCountedFolders()

    fun getWordCards(): Flow<List<WordCard>> = database.wordDao().getWordCards()
    suspend fun getWordCard(cardID: UUID): WordCard = database.wordDao().getWordCard(cardID)
    fun getBookmarkedWords(): Flow<List<Word>> = database.wordDao().getBookmarkedWords()
    suspend fun wordExist(name: String): Boolean = database.wordDao().wordExist(name)
    fun deleteWordCard(cardId: UUID) {
        coroutineScope.launch {
            database.wordDao().deleteWordCard(cardId)
        }
    }

    fun deleteFolder(folderID: UUID) {
        coroutineScope.launch {
            database.wordDao().deleteFolder(folderID)
        }
    }

    fun addWordCard(
        pos: String,
        def: String,
        example: Array<String>,
        synonyms: Array<String>,
        antonyms: Array<String>,
        word: String,
        folder: UUID
    ) {
        coroutineScope.launch {
            val wordWord: Word = getWord(word)
            addWordCard(WordCard(pos, def, example, synonyms, antonyms, wordWord, folder))
        }
    }

    fun updateWordCard(
        pos: String,
        def: String,
        example: Array<String>,
        synonyms: Array<String>,
        antonyms: Array<String>,
        word: String,
        folder: UUID,
    ) {
        coroutineScope.launch {
            val wordWord: Word = getWord(word)
            addWordCard(WordCard(pos, def, example, synonyms, antonyms, wordWord, folder))
        }
    }

    suspend fun addOrUpdateWord(word: Word) {
        if (wordExist(word.word))
            updateWord(word)
        else
            addWord(word)
    }

    fun updateCardsInfo(cards: Array<UUID>, correctness: Array<Boolean>, testType: TestType) {
        coroutineScope.launch {
            cards.forEachIndexed { index, uuid ->
                updateWordCard(QuestionManager.submitAnswer(getWordCard(uuid), correctness[index], testType))
            }
        }
    }

    fun deleteUnusedWords() {
        coroutineScope.launch {
            database.wordDao().deleteUnusedWords()
        }
    }

    companion object {
        private var INSTANCE: WordRepository? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = WordRepository(context)
            }
        }

        fun get(): WordRepository {
            return INSTANCE ?: throw IllegalStateException("WordRepository must be initialized")
        }
    }
}