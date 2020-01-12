package com.lookie.socialdownloader.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.lookie.socialdownloader.data.room.entity.User

/**
 * @author Phung Nguyen on 2020-01-12 14:17
 **/
@Dao
interface UserDao {

  @Query("SELECT * FROM users ORDER BY createAt DESC")
  fun getUsers(): LiveData<List<User>>

  @Query("SELECT * FROM users WHERE id = :id")
  fun getUserDetails(id: String): User

  @Query("SELECT * FROM users ORDER BY createAt DESC LIMIT 1")
  fun getLastUser(): LiveData<User>

  @Query("SELECT EXISTS(SELECT 1 FROM users WHERE id = :id LIMIT 1)")
  fun isExists(id: String): Boolean

  @Insert
  fun insertUser(post: User)

  @Delete
  fun deleteUser(post: User)
}