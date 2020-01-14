package com.lookie.instadownloader.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.databinding.DataBindingUtil
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.SkuType
import com.lookie.instadownloader.R
import com.lookie.instadownloader.base.BaseActivity
import com.lookie.instadownloader.databinding.ActivityMainBinding
import com.lookie.instadownloader.ui.custom.MyViewPagerAdapter
import com.lookie.instadownloader.ui.download.DownloadFragment
import com.lookie.instadownloader.ui.home.HomeFragment
import com.lookie.instadownloader.utilities.SharedPrefUtils
import com.lookie.instadownloader.utilities.SystemUtils

class MainActivity : BaseActivity(), PurchasesUpdatedListener, BillingClientStateListener {

  private var mBinding: ActivityMainBinding? = null

  private var adapter: MyViewPagerAdapter? = null

  private var mBillingClient: BillingClient? = null

  private var mSkuDetails: SkuDetails? = null

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

    // billing
    mBillingClient =
      BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build()
    mBillingClient!!.startConnection(this)
  }

  @SuppressLint("ResourceType")
  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    Log.w(TAG, "onNewIntent")
    var link = ""
    if (intent != null && intent.extras != null) {
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
    private const val TAG = "MainActivity"
    private const val ITEM_SKU_UPGRADE = "upgrade_premium"
    private const val REQUEST_READ_STORAGE = 1
    private const val REQUEST_WRITE_STORAGE = 2
  }

  fun doUpgrade() {
//    if (mSkuDetails != null) {
//      mBillingClient!!.launchBillingFlow(
//        this,
//        BillingFlowParams.newBuilder().setSkuDetails(mSkuDetails).build()
//      )
//    }
    Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show()
  }

  private fun restartApp() {
    val intent = Intent(this, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)
    finish()
  }

  override fun onPurchasesUpdated(
    billingResult: BillingResult?,
    purchases: MutableList<Purchase>?
  ) {
    Log.w(TAG, "onPurchasesUpdated")
    val resCode: Int = billingResult!!.responseCode
    if (resCode == BillingClient.BillingResponseCode.OK && purchases != null) {
      for (purchase in purchases) {
        if (ITEM_SKU_UPGRADE == purchase.sku) {
          upgradePremiumSuccess()
        }
      }
    } else if (resCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
      upgradePremiumSuccess()
    }
  }

  private fun upgradePremiumSuccess() {
    Toast.makeText(this, "Upgrade Premium Success", Toast.LENGTH_SHORT).show()
    SharedPrefUtils.instance!!.setPremium(true)
    restartApp()
  }

  override fun onBillingServiceDisconnected() {
    Log.w(TAG, "onBillingServiceDisconnected")
  }

  override fun onBillingSetupFinished(billingResult: BillingResult?) {
    Log.w(TAG, "onBillingSetupFinished")
    if (billingResult!!.responseCode == BillingClient.BillingResponseCode.OK) {
      queryPurchases()
    }
  }

  private fun queryPurchases() {
    Log.w(TAG, "queryPurchases")
    val params = SkuDetailsParams.newBuilder()
    params.setSkusList(listOf(ITEM_SKU_UPGRADE)).setType(SkuType.INAPP)
    mBillingClient!!.querySkuDetailsAsync(params.build()) { _: BillingResult?, skuDetailsList: List<SkuDetails> ->
      for (skuDetails in skuDetailsList) {
        Log.w(TAG, "querySkuDetailsAsync skuDetails: " + skuDetails.originalJson)
        if (ITEM_SKU_UPGRADE == skuDetails.sku) {
          mSkuDetails = skuDetails
        }
      }
    }
  }
}
