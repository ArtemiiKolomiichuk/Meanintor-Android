package com.example.practiceeng

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.*


@Database(entities = [WordCard::class, Folder::class, Word::class], version = 1)
@TypeConverters(EntityConverter::class)
abstract class WordDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao
}

class EntityConverter {
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
            typesArray += (Pair(TestType.values()[splitType[0].toInt()], splitType[1].toInt()))
        }
        val lastDate = Date(split[1].toLong())
        return TrainingHistory(typesArray, lastDate)
    }

    @TypeConverter
    fun fromStringArray(value: Array<String>): String {
        return value.joinToString("#")
    }

    @TypeConverter
    fun toStringArray(value: String): Array<String> {
        return value.split("#").toTypedArray()
    }

    @TypeConverter
    fun fromUUID(value: UUID): String {
        return value.toString()
    }

    @TypeConverter
    fun toUUID(value: String): UUID {
        return UUID.fromString(value)
    }
}

@Dao
interface WordDao {
    @Query("SELECT * FROM word")
    fun getWords(): Flow<List<Word>>

    @Query("SELECT * FROM word WHERE wordID=(:id)")
    fun getWord(id: UUID): Flow<Word?>
}
