package com.lookie.socialdownloader.data.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class ShortMediaModel() : Parcelable {

  @SerializedName("id")
  var id: String? = ""

  @SerializedName("shortcode")
  var shortcode: String? = ""

  @SerializedName("display_url")
  var displayUrl: String? = ""

  @SerializedName("video_url")
  var videoUrl: String? = ""

  @SerializedName("is_video")
  var isVideo: Boolean = false

  @SerializedName("edge_sidecar_to_children")
  var children: ChildrenModel? = ChildrenModel()

  @SerializedName("edge_media_to_caption")
  var caption: ChildrenModel? = ChildrenModel()

  @SerializedName("owner")
  var owner: OwnerModel? = OwnerModel()

  @SerializedName("text")
  var text: String? = ""

  constructor(parcel: Parcel) : this() {
    id = parcel.readString()
    shortcode = parcel.readString()
    displayUrl = parcel.readString()
    videoUrl = parcel.readString()
    isVideo = parcel.readByte() != 0.toByte()
    children = parcel.readParcelable(ChildrenModel::class.java.classLoader)
    caption = parcel.readParcelable(ChildrenModel::class.java.classLoader)
    text = parcel.readString()
  }

  override fun toString(): String {
    return Gson().toJson(this)
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(id)
    parcel.writeString(shortcode)
    parcel.writeString(displayUrl)
    parcel.writeString(videoUrl)
    parcel.writeByte(if (isVideo) 1 else 0)
    parcel.writeParcelable(children, flags)
    parcel.writeParcelable(caption, flags)
    parcel.writeString(text)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<ShortMediaModel> {
    override fun createFromParcel(parcel: Parcel): ShortMediaModel {
      return ShortMediaModel(parcel)
    }

    override fun newArray(size: Int): Array<ShortMediaModel?> {
      return arrayOfNulls(size)
    }
  }
}