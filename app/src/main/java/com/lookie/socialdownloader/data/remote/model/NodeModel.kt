package com.lookie.socialdownloader.data.remote.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class NodeModel {

  @SerializedName("id")
  var id: String? = null

  @SerializedName("shortcode")
  var shortcode: String? = null

  @SerializedName("display_url")
  var displayUrl: String? = null

  @SerializedName("video_url")
  var videoUrl: String? = null

  @SerializedName("is_video")
  var isVideo: Boolean = false

  @SerializedName("edge_sidecar_to_children")
  var children: ChildrenModel? = null

  override fun toString(): String {
    return Gson().toJson(this)
  }
}