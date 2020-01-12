package com.lookie.socialdownloader.data.remote.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class GrapqlModel {

  @SerializedName("shortcode_media")
  var shortMedia: ShortMediaModel? = null

  override fun toString(): String {
    return Gson().toJson(this)
  }
}