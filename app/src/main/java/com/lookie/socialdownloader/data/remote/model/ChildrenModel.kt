package com.lookie.socialdownloader.data.remote.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class ChildrenModel {

  @SerializedName("edges")
  val edges: List<EdgeModel>? = mutableListOf()

  override fun toString(): String {
    return Gson().toJson(this)
  }
}