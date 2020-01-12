package com.lookie.socialdownloader.data.remote.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class EdgeModel {

  @SerializedName("node")
  val note: ShortMediaModel? = ShortMediaModel()

  override fun toString(): String {
    return Gson().toJson(this)
  }
}