package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.BookmarksDao
import com.example.data.dao.HistoryDao
import com.example.data.model.BookmarkEntity
import com.example.data.model.HistoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [BookmarkEntity::class, HistoryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarksDao(): BookmarksDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "agron_browser.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate default speed dials for TV
                        CoroutineScope(Dispatchers.IO).launch {
                            val defaultSpeedDials = listOf(
                                BookmarkEntity(
                                    title = "Google",
                                    url = "https://www.google.com",
                                    isSpeedDial = true
                                ),
                                BookmarkEntity(
                                    title = "YouTube",
                                    url = "https://www.youtube.com",
                                    isSpeedDial = true
                                ),
                                BookmarkEntity(
                                    title = "Wikipedia",
                                    url = "https://tr.wikipedia.org",
                                    isSpeedDial = true
                                ),
                                BookmarkEntity(
                                    title = "Haberler",
                                    url = "https://news.google.com",
                                    isSpeedDial = true
                                ),
                                BookmarkEntity(
                                    title = "Reddit",
                                    url = "https://www.reddit.com",
                                    isSpeedDial = true
                                ),
                                BookmarkEntity(
                                    title = "GitHub",
                                    url = "https://github.com",
                                    isSpeedDial = true
                                )
                            )
                            getInstance(context).bookmarksDao().insertAll(defaultSpeedDials)
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
