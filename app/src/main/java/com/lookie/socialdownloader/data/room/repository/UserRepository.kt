package com.lookie.socialdownloader.data.room.repository

import com.lookie.socialdownloader.data.room.entity.User
import com.lookie.socialdownloader.data.room.dao.UserDao

/**
 * @author Phung Nguyen on 2020-01-12 14:20
 **/
class UserRepository private constructor(private val userDao: UserDao) {

  fun getUsers() = userDao.getUsers()

  fun getLastUser() = userDao.getLastUser()

  fun isExists(id: String): Boolean {
    return userDao.isExists(id)
  }

  fun getUserDetails(id: String): User {
    return userDao.getUserDetails(id)
  }

  fun insertUser(post: User) {
    userDao.insertUser(post)
  }

  fun deleteUser(post: User) {
    userDao.deleteUser(post)
  }

  companion object {

    // For Singleton instantiation
    @Volatile
    private var instance: UserRepository? = null

    fun getInstance(userDao: UserDao) =
      instance
        ?: synchronized(this) {
        instance
          ?: UserRepository(
            userDao
          ).also { instance = it }
      }
  }
}