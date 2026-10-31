package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_songs")
data class StudySongEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val subject: String,
    val genreId: String,
    val genreTitle: String,
    val language: String,
    val mood: String,
    val durationSeconds: Int,
    val originalStudyText: String,
    val factPreservationScore: Int,
    val preservedFactsCsv: String,
    val verse1: String,
    val chorus: String,
    val verse2: String,
    val bridge: String,
    val outro: String,
    val audioFilePath: String,
    val isFavorite: Boolean = false,
    val playCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
