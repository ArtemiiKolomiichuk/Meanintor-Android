package com.example.practiceeng.database

import androidx.room.*
import com.example.practiceeng.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.internal.toLongOrDefault
import java.util.*

@Database(entities = [ Word::class, WordCard::class, Folder::class ], version=1)
@TypeConverters(WordTypeConverters::class)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}

class WordTypeConverters {
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }
    @TypeConverter
    fun toDate(millisSinceEpoch: Long): Date {
        return Date(millisSinceEpoch)
    }

    @TypeConverter
    fun fromTrainingHistory(value: TrainingHistory): String {
        var string = ""
        for (i in value.types.indices) {
            string += value.types[i].first.ordinal.toString() + "-" + value.types[i].second.toString()
            if (i != value.types.size - 1) {
                string += "#"
            }
        }
        string += "|${value.lastDate?.time}"
        return string
    }

    @TypeConverter
    fun toTrainingHistory(value: String): TrainingHistory {
            val split = value.split("|")
            val types = split[0].split("#")
            var typesArray = arrayOf<Pair<TestType, Int>>()
            for (type in types) {
                val splitType = type.split("-")
                if(splitType[0].isNotEmpty()&&splitType[1].isNotEmpty())
                typesArray += (Pair(TestType.values()[splitType[0].toInt()], splitType[1].toInt()))
            }
            val lastDate = Date(split[1].toLongOrDefault(Date().time))
            return TrainingHistory(typesArray, lastDate)
    }

    @TypeConverter
    fun toString(value: Word): String {
       return value.word.toString()
    }

    @TypeConverter
    fun toWord(value: String): Word {
       return WordRepository.get().getWord(value)
    }

    @TypeConverter
    fun fromStringArray(value: Array<String>): String {
        return value.joinToString("#")
    }

    @TypeConverter
    fun toStringArray(value: String): Array<String> {
        return value.split("#").toTypedArray()
    }
}


@Dao
interface WordDao {
    @Update
    suspend fun updateWord(word: Word)
    @Insert
    suspend fun addWord(word: Word)
    @Update
    suspend fun updateFolder(folder: Folder)
    @Insert
    suspend fun addFolder(folder: Folder)
    @Update
    suspend fun updateWordCard(wordCard: WordCard)
    @Insert
    suspend fun addWordCard(wordCard: WordCard)
    @Query("SELECT * FROM word")
    fun getWords(): Flow<List<Word>>


    @Query("SELECT * FROM word WHERE word=(:name)")
    fun getWord(name: String): Word

    @Query("SELECT EXISTS(SELECT * FROM word WHERE word=(:name))")
    suspend fun wordExist(name : String) : Boolean

    @Query("SELECT * FROM folder")
    fun getFolders(): Flow<List<Folder>>

    @Query("SELECT * FROM folder WHERE folderID=(:folderID)")
    suspend fun getFolder(folderID: UUID): Folder

    @Query("SELECT folder.*, COUNT(wordCard.folderID) AS amount FROM folder LEFT JOIN wordCard ON folder.folderID = wordCard.folderID GROUP BY folder.folderID")
    fun getCountedFolders(): Flow<List<CountedFolder>>

    @Query("SELECT * FROM wordCard WHERE folderID=(:folderID)")
    fun getWordCardsFromFolder(folderID: UUID): Flow<List<WordCard>>

    @Query("SELECT * FROM wordCard")
    fun getWordCards(): Flow<List<WordCard>>

    @Query("SELECT * FROM wordCard WHERE cardID=(:cardID)")
    suspend fun getWordCard(cardID: UUID): WordCard

    @Query("SELECT * FROM word WHERE bookmarked=true")
    fun getBookmarkedWords(): Flow<List<Word>>


}