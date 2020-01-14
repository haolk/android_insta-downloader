package com.lookie.socialdownloader.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import com.lookie.socialdownloader.R
import com.lookie.socialdownloader.base.BaseActivity
import com.lookie.socialdownloader.databinding.ActivityMainBinding
import com.lookie.socialdownloader.ui.download.DownloadFragment
import com.lookie.socialdownloader.ui.home.HomeFragment
import com.lookie.socialdownloader.ui.custom.MyViewPagerAdapter
import com.lookie.socialdownloader.utilities.SystemUtils

class MainActivity : BaseActivity() {

  private var mBinding: ActivityMainBinding? = null
  private var adapter: MyViewPagerAdapter? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

    Log.w(TAG, "onCreate")

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      isReadStoragePermissionGranted
      isWriteStoragePermissionGranted
    }

    var link = ""
    if (intent != null && intent.extras != null) {
      val bundle = intent.extras
      for (key in bundle!!.keySet()) {
        val value = bundle[key]
        Log.w(TAG, key + ": " + value.toString())
      }

      // get link from insta app
      link = intent.extras!!.getString("android.intent.extra.TEXT", "")
    }

    Log.w(TAG, "link $link")

    // setup ViewPager
    adapter = MyViewPagerAdapter(supportFragmentManager)
    val homeFragment = HomeFragment.newInstance(link)
    val downloadFragment = DownloadFragment()
    adapter!!.addFragment(homeFragment)
    adapter!!.addFragment(downloadFragment)
    mBinding!!.viewpager.adapter = adapter
    mBinding!!.viewpager.setPagingEnabled(false)

    // event of BottomNavigationView
    mBinding!!.bottomNavigation.setOnNavigationItemSelectedListener {
      mBinding!!.bottomNavigation.menu.findItem(it.itemId).isChecked = true
      when (it.itemId) {
        R.id.navigation_home -> {
          mBinding!!.viewpager.currentItem = 0
        }
        R.id.navigation_download -> {
          mBinding!!.viewpager.currentItem = 1
        }
      }
      false
    }
  }

  @SuppressLint("ResourceType")
  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    Log.w(TAG, "onNewIntent")
    var link = ""
    if (intent != null && intent.extras != null) {
      val bundle = intent.extras
      for (key in bundle!!.keySet()) {
        val value = bundle[key]
        Log.w(TAG, key + ": " + value.toString())
      }

      // get link from insta app
      link = intent.extras!!.getString("android.intent.extra.TEXT", "")
    }

    Log.w(TAG, "link $link")

    if (!TextUtils.isEmpty(link)) {
      val frag = adapter!!.mFragmentList[0]
      if (frag is HomeFragment) {
        frag.setLink(link)
      }
    }
  }

  private fun selfPermissionGranted(permission: String?): Boolean {
    // For Android < Android M, self permissions are always granted.
    var result = true
    val targetSdkVersion = SystemUtils.getTargetSdkVersion(this)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      result =
        if (targetSdkVersion >= Build.VERSION_CODES.M) {
          // targetSdkVersion >= Android M, we can
          (checkSelfPermission(permission!!)
              == PackageManager.PERMISSION_GRANTED)
        } else {
          // targetSdkVersion < Android M, we have to use PermissionChecker
          (PermissionChecker.checkSelfPermission(this, permission!!)
              == PermissionChecker.PERMISSION_GRANTED)
        }
    }
    return result
  }

  //permission is automatically granted on sdk < 23 upon installation
  val isReadStoragePermissionGranted: Boolean
    get() = if (Build.VERSION.SDK_INT >= 23) {
      if (selfPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
        Log.v(TAG, "Permission is granted1")
        true
      } else {
        Log.v(TAG, "Permission is revoked1")
        ActivityCompat
          .requestPermissions(
            this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_READ_STORAGE
          )
        false
      }
    } else { //permission is automatically granted on sdk < 23 upon installation
      Log.v(TAG, "Permission is granted1")
      true
    }

  //permission is automatically granted on sdk < 23 upon installation
  val isWriteStoragePermissionGranted: Boolean
    get() = if (Build.VERSION.SDK_INT >= 23) {
      if (selfPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        Log.v(TAG, "Permission is granted2")
        true
      } else {
        Log.v(TAG, "Permission is revoked2")
        ActivityCompat
          .requestPermissions(
            this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_WRITE_STORAGE
          )
        false
      }
    } else { //permission is automatically granted on sdk < 23 upon installation
      Log.v(TAG, "Permission is granted2")
      true
    }

  companion object {
    private const val TAG = "HomeFragment"
    private const val REQUEST_READ_STORAGE = 1
    private const val REQUEST_WRITE_STORAGE = 2
  }
}
