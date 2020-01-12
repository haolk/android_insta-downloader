package com.lookie.socialdownloader.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.lookie.socialdownloader.data.remote.model.ChildrenModel
import com.lookie.socialdownloader.data.remote.model.OwnerModel
import com.lookie.socialdownloader.data.room.Converters

@Entity(tableName = "posts")
data class Post(
  @PrimaryKey
  val id: String = "",
  var shortcode: String = "",
  var displayUrl: String = "",
  var videoUrl: String = "",
  var isVideo: Boolean = false,
  var text: String = "",
  var createAt: String = "",
  @TypeConverters(Converters::class)
  var children: ChildrenModel,
  @TypeConverters(Converters::class)
  var caption: ChildrenModel,
  @TypeConverters(Converters::class)
  var owner: OwnerModel
)
