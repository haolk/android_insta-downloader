package com.lookie.instadownloader.utilities

import android.content.SharedPreferences
import com.lookie.instadownloader.base.MyApplication

class SharedPrefUtils private constructor() {

  private val mPreferences: SharedPreferences =
    MyApplication.getInstance()!!.getSharedPreferences("insta-downloader", 0)

  private fun getString(key: String?): String? {
    return mPreferences.getString(key, "")
  }

  private fun getString(key: String?, defaultValue: String?): String? {
    return mPreferences.getString(key, defaultValue)
  }

  private fun setString(key: String, content: String) {
    mPreferences.edit().putString(key, content).apply()
  }

  private fun getBoolean(key: String): Boolean {
    return mPreferences.getBoolean(key, false)
  }

  private fun setBoolean(key: String, b: Boolean) {
    mPreferences.edit().putBoolean(key, b).apply()
  }

  fun setPremium(premium: Boolean) {
    setBoolean(PREMIUM, premium)
  }

  val premium: Boolean? get() = getBoolean(PREMIUM)

  companion object {
    private const val PREMIUM = "premium"
    private var mInstance: SharedPrefUtils? = null

    @JvmStatic
    @get:Synchronized
    val instance: SharedPrefUtils?
      get() {
        if (mInstance == null) {
          mInstance = SharedPrefUtils()
        }
        return mInstance
      }
  }

}