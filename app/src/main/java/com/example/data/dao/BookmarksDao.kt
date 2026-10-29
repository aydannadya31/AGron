package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarksDao {
    @Query("SELECT * FROM bookmarks ORDER BY isSpeedDial DESC, createdAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE isSpeedDial = 1 ORDER BY createdAt ASC")
    fun getSpeedDials(): Flow<List<BookmarkEntity>>

    @Query("SELECT COUNT(*) FROM bookmarks")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: BookmarkEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bookmarks: List<BookmarkEntity>)

    @Update
    suspend fun update(bookmark: BookmarkEntity)

    @Delete
    suspend fun delete(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE url = :url")
    suspend fun deleteByUrl(url: String)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE url = :url)")
    suspend fun isBookmarked(url: String): Boolean
}
