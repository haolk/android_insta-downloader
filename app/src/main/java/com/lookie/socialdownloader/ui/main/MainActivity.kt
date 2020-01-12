package com.lookie.socialdownloader.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.lookie.socialdownloader.R
import com.lookie.socialdownloader.base.BaseActivity
import com.lookie.socialdownloader.utilities.SystemUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val navController = findNavController(R.id.nav_host_fragment)
    // Passing each menu ID as a set of Ids because each
    // menu should be considered as top level destinations.
    val appBarConfiguration = AppBarConfiguration(
      setOf(R.id.navigation_home, R.id.navigation_download)
    )
    setupActionBarWithNavController(navController, appBarConfiguration)
    bottom_nav.setupWithNavController(navController)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      isReadStoragePermissionGranted
      isWriteStoragePermissionGranted
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
