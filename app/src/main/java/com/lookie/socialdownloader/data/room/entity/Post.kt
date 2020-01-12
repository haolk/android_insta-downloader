package com.lookie.socialdownloader.data.room.entity

import android.os.Parcel
import android.os.Parcelable
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
) : Parcelable {
  constructor(parcel: Parcel) : this(
    parcel.readString()!!,
    parcel.readString()!!,
    parcel.readString()!!,
    parcel.readString()!!,
    parcel.readByte() != 0.toByte(),
    parcel.readString()!!,
    parcel.readString()!!,
    parcel.readParcelable(ChildrenModel::class.java.classLoader)!!,
    parcel.readParcelable(ChildrenModel::class.java.classLoader)!!,
    parcel.readParcelable(OwnerModel::class.java.classLoader)!!
  )

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(id)
    parcel.writeString(shortcode)
    parcel.writeString(displayUrl)
    parcel.writeString(videoUrl)
    parcel.writeByte(if (isVideo) 1 else 0)
    parcel.writeString(text)
    parcel.writeString(createAt)
    parcel.writeParcelable(children, flags)
    parcel.writeParcelable(caption, flags)
    parcel.writeParcelable(owner, flags)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<Post> {
    override fun createFromParcel(parcel: Parcel): Post {
      return Post(parcel)
    }

    override fun newArray(size: Int): Array<Post?> {
      return arrayOfNulls(size)
    }
  }
}
