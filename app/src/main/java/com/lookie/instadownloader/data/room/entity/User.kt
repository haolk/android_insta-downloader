package com.lookie.instadownloader.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
  @PrimaryKey
  val id: String = "",
  var profilePicUrl: String = "",
  var username: String = "",
  var fullName: String = "",
  var createAt: String = ""
)
