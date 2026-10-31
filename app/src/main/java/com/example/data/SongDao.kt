package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM study_songs ORDER BY createdAt DESC")
    fun getAllSongsFlow(): Flow<List<StudySongEntity>>

    @Query("SELECT * FROM study_songs WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteSongsFlow(): Flow<List<StudySongEntity>>

    @Query("SELECT * FROM study_songs WHERE subject = :subject ORDER BY createdAt DESC")
    fun getSongsBySubjectFlow(subject: String): Flow<List<StudySongEntity>>

    @Query("SELECT * FROM study_songs WHERE id = :songId")
    suspend fun getSongById(songId: Long): StudySongEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: StudySongEntity): Long

    @Update
    suspend fun updateSong(song: StudySongEntity)

    @Delete
    suspend fun deleteSong(song: StudySongEntity)

    @Query("UPDATE study_songs SET isFavorite = :isFav WHERE id = :songId")
    suspend fun updateFavorite(songId: Long, isFav: Boolean)

    @Query("UPDATE study_songs SET playCount = playCount + 1 WHERE id = :songId")
    suspend fun incrementPlayCount(songId: Long)

    @Query("SELECT COUNT(*) FROM study_songs")
    suspend fun getCount(): Int
}
