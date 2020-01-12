package com.lookie.socialdownloader.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lookie.socialdownloader.data.room.dao.PostDao
import com.lookie.socialdownloader.data.room.dao.UserDao
import com.lookie.socialdownloader.data.room.entity.Post
import com.lookie.socialdownloader.data.room.entity.User
import com.lookie.socialdownloader.utilities.DATABASE_NAME
import com.lookie.socialdownloader.utilities.DATABASE_VERSION

/**
 * @author Phung Nguyen on 2020-01-11 21:29
 **/
@Database(entities = [Post::class, User::class], version = DATABASE_VERSION, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

  abstract fun postDao(): PostDao

  abstract fun userDao(): UserDao

  companion object {
    @Volatile
    private var instance: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
      return instance ?: synchronized(this) {
        instance ?: buildDatabase(context).also { instance = it }
      }
    }

    private fun buildDatabase(context: Context): AppDatabase {
      return Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        DATABASE_NAME
      ).build()
    }
  }
}