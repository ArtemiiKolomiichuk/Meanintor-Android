package com.example.practiceeng.database

import android.content.Context
import androidx.room.Room
import com.example.practiceeng.WordDatabase
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

  /*  fun getCrimes(): Flow<List<Crime>> = database.crimeDao().getCrimes()
    suspend fun getCrime(id: UUID): Crime = database.crimeDao().getCrime(id)
    fun updateCrime(crime:Crime) {
        coroutineScope.launch {
            database.crimeDao().updateCrime(crime)
        }
    }
    suspend fun addCrime(crime: Crime){
        database.crimeDao().addCrime(crime)
    }*/

    companion object {
        private var INSTANCE: WordRepository? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = WordRepository(context)
            }
        }
        fun get(): WordRepository {
            return INSTANCE ?:
            throw IllegalStateException("WordRepository must be initialized")
        }
    }
}