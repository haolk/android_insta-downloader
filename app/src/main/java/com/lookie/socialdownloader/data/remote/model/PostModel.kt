package com.lookie.socialdownloader.data.remote.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class PostModel {

  @SerializedName("id")
  var id: String? = null

  @SerializedName("shortcode")
  var shortcode: String? = null

  @SerializedName("media_preview")
  var mediaPreview: String? = null

  @SerializedName("display_url")
  var displayUrl: String? = null

  @SerializedName("is_video")
  var isVideo: String? = null

  @SerializedName("is_ad")
  var isAd: Boolean? = null

  override fun toString(): String {
    return Gson().toJson(this)
  }
}