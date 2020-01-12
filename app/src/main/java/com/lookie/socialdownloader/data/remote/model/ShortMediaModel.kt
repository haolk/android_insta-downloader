package com.lookie.socialdownloader.data.remote.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class ShortMediaModel {

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

  override fun toString(): String {
    return Gson().toJson(this)
  }
}