package com.lookie.socialdownloader.data.remote.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class InstaModel {

  @SerializedName("graphql")
  var grapql: GrapqlModel? = null

  override fun toString(): String {
    return Gson().toJson(this)
  }
}