package com.lookie.socialdownloader.data.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class ChildrenModel() : Parcelable {

  @SerializedName("edges")
  var edges: List<EdgeModel>? = mutableListOf()

  constructor(parcel: Parcel) : this() {
    edges = parcel.createTypedArrayList(EdgeModel.CREATOR)
  }

  override fun toString(): String {
    return Gson().toJson(this)
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeTypedList(edges)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<ChildrenModel> {
    override fun createFromParcel(parcel: Parcel): ChildrenModel {
      return ChildrenModel(parcel)
    }

    override fun newArray(size: Int): Array<ChildrenModel?> {
      return arrayOfNulls(size)
    }
  }
}