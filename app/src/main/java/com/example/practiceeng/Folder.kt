package com.example.practiceeng

import java.util.UUID

/**
 * Folder containing [WordCard]s
 */
data class Folder
constructor(
    var title : String,
    var description : String,
    var folderID : UUID = UUID.randomUUID())

