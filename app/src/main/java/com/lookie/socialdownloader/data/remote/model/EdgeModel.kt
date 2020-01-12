package com.lookie.socialdownloader.data.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class EdgeModel() : Parcelable {

  @SerializedName("node")
  var note: ShortMediaModel? = ShortMediaModel()

  constructor(parcel: Parcel) : this() {
    note = parcel.readParcelable(ShortMediaModel::class.java.classLoader)
  }

  override fun toString(): String {
    return Gson().toJson(this)
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeParcelable(note, flags)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<EdgeModel> {
    override fun createFromParcel(parcel: Parcel): EdgeModel {
      return EdgeModel(parcel)
    }

    override fun newArray(size: Int): Array<EdgeModel?> {
      return arrayOfNulls(size)
    }
  }
}