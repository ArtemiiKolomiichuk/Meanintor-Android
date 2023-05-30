package com.example.practiceeng

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Folder containing [WordCard]s
 */
@Entity
data class Folder(
    var title : String,
    var description : String,
    var paused: Boolean = false,
    @PrimaryKey var folderID : UUID = UUID.randomUUID())

