package com.lookie.instadownloader.data.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class OwnerModel() : Parcelable {

  @SerializedName("id")
  var id: String? = null

  @SerializedName("profile_pic_url")
  var profilePicUrl: String? = null

  @SerializedName("username")
  var username: String? = null

  @SerializedName("full_name")
  var fullName: String? = null

  constructor(parcel: Parcel) : this() {
    id = parcel.readString()
    profilePicUrl = parcel.readString()
    username = parcel.readString()
    fullName = parcel.readString()
  }

  override fun toString(): String {
    return Gson().toJson(this)
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(id)
    parcel.writeString(profilePicUrl)
    parcel.writeString(username)
    parcel.writeString(fullName)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<OwnerModel> {
    override fun createFromParcel(parcel: Parcel): OwnerModel {
      return OwnerModel(parcel)
    }

    override fun newArray(size: Int): Array<OwnerModel?> {
      return arrayOfNulls(size)
    }
  }
}