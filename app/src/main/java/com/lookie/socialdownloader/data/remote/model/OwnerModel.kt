package com.lookie.socialdownloader.data.remote.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class OwnerModel {

  @SerializedName("id")
  var id: String? = null

  @SerializedName("profile_pic_url")
  var profilePicUrl: String? = null

  @SerializedName("username")
  var username: String? = null

  @SerializedName("full_name")
  var fullName: String? = null

  override fun toString(): String {
    return Gson().toJson(this)
  }
}