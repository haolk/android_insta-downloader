package com.lookie.instadownloader.base

import android.app.Application

class MyApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    instance = this
  }

  companion object {
    private var instance: MyApplication? = null
    @JvmStatic
    fun getInstance(): MyApplication? {
      return instance
    }
  }
}