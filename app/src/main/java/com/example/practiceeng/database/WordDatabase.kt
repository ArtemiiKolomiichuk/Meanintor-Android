package com.example.practiceeng.database

import androidx.room.*
import com.example.practiceeng.Folder
import com.example.practiceeng.TrainingHistory
import com.example.practiceeng.Word
import com.example.practiceeng.WordCard
import java.util.*

@Database(entities = [ Word::class, WordCard::class, Folder::class ], version=1)
@TypeConverters(CrimeTypeConverters::class)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}

class CrimeTypeConverters {
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }
    @TypeConverter
    fun toDate(millisSinceEpoch: Long): Date {
        return Date(millisSinceEpoch)
    }

    @TypeConverter
    fun toArray(array: String): Array<String> {
        return emptyArray()
    }

    @TypeConverter
    fun fromArray(array: Array<String>): String{
        return String()
    }
    @TypeConverter
    fun toTrainingHistory(history: String): TrainingHistory {
        return TrainingHistory()
    }
    fun fromTrainingHistory(history: TrainingHistory): String {
        return history.toString()
    }
}

@Dao
interface WordDao {

   /* @Query("SELECT * FROM crime")
    fun getCrimes(): Flow<List<Crime>>
    @Query("SELECT * FROM crime WHERE id=(:id)")
    suspend fun getCrime(id: UUID): Crime
    @Update
    suspend fun updateCrime(crime: Crime)
    @Insert
    suspend fun addCrime(crime: Crime)*/
}