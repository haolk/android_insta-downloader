package com.lookie.instadownloader.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.lookie.instadownloader.R
import com.lookie.instadownloader.ui.custom.InterstitialAdCallback
import com.lookie.instadownloader.ui.custom.RewardedVideoAdCallback
import com.lookie.instadownloader.utilities.SharedPrefUtils
import io.fabric.sdk.android.Fabric

open class BaseActivity : AppCompatActivity() {

  private var mInterstitialAd: InterstitialAd? = null

  private var mRewardedVideoAd: RewardedVideoAd? = null

  private val adListener = object : AdListener() {

    override fun onAdLoaded() {
      // Code to be executed when an ad finishes loading.
      Log.e(TAG, "onAdLoaded")
    }

    override fun onAdFailedToLoad(errorCode: Int) {
      // Code to be executed when an ad request fails.
      Log.e(TAG, "onAdFailedToLoad: $errorCode")
    }

    override fun onAdOpened() {
      // Code to be executed when the ad is displayed.
      Log.e(TAG, "onAdOpened")
    }

    override fun onAdLeftApplication() {
      // Code to be executed when the user has left the app.
      Log.e(TAG, "onAdLeftApplication")
    }

    override fun onAdClosed() {
      // Code to be executed when when the interstitial ad is closed.
      Log.e(TAG, "onAdClosed")
      if (interstitialAdCallback != null) {
        interstitialAdCallback!!.onAdClosed()
      }
      loadInterstitialAd()
    }
  }

  private val rewardedVideoAdListener = object : RewardedVideoAdListener {
    override fun onRewardedVideoAdClosed() {
      Log.w(TAG, "onRewardedVideoAdClosed")
      if (rewardedVideoAdCallback != null) {
        rewardedVideoAdCallback!!.onRewardedVideoAdClosed()
      }
      loadRewardedVideoAd()
    }

    override fun onRewardedVideoAdLeftApplication() {
      Log.w(TAG, "onRewardedVideoAdLeftApplication")
    }

    override fun onRewardedVideoAdLoaded() {
      Log.w(TAG, "onRewardedVideoAdLoaded")
    }

    override fun onRewardedVideoAdOpened() {
      Log.w(TAG, "onRewardedVideoAdOpened")
    }

    override fun onRewardedVideoCompleted() {
      Log.w(TAG, "onRewardedVideoCompleted")
      if (rewardedVideoAdCallback != null) {
        rewardedVideoAdCallback!!.onRewardedVideoCompleted()
      }
    }

    override fun onRewarded(p0: RewardItem?) {
      Log.w(TAG, "onRewarded")
    }

    override fun onRewardedVideoStarted() {
      Log.w(TAG, "onRewardedVideoStarted")
    }

    override fun onRewardedVideoAdFailedToLoad(p0: Int) {
      Log.w(TAG, "onRewardedVideoAdFailedToLoad")
    }
  }

  var interstitialAdCallback: InterstitialAdCallback? = null

  var rewardedVideoAdCallback: RewardedVideoAdCallback? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    // init Crashlytics
    Fabric.with(this, Crashlytics())

    // init MobileAds
    MobileAds.initialize(this, getString(R.string.admob_app_id))

    loadInterstitialAd()

    loadRewardedVideoAd()
  }

  fun showInterstitialAds(callback: InterstitialAdCallback) {
    Log.e(TAG, "showInterstitialAds")
    interstitialAdCallback = callback

    if (SharedPrefUtils.instance!!.premium!!) {
      interstitialAdCallback!!.onAdClosed()
    } else {
      if (mInterstitialAd!!.isLoaded) {
        mInterstitialAd!!.show()
      } else {
        loadInterstitialAd()
      }
    }
  }

  fun showRewardedVideoAd(callback: RewardedVideoAdCallback) {
    Log.e(TAG, "showRewardedVideoAd")
    rewardedVideoAdCallback = callback

    if (SharedPrefUtils.instance!!.premium!!) {
      rewardedVideoAdCallback!!.onRewardedVideoCompleted()
    } else {
      if (mRewardedVideoAd != null && mRewardedVideoAd!!.isLoaded) {
        mRewardedVideoAd!!.show()
      } else {
        loadRewardedVideoAd()
      }
    }
  }

  override fun onResume() {
    super.onResume()
    if (mRewardedVideoAd != null) {
      mRewardedVideoAd!!.pause(this)
    }
  }

  override fun onPause() {
    super.onPause()
    if (mRewardedVideoAd != null) {
      mRewardedVideoAd!!.resume(this)
    }
  }

  private fun loadInterstitialAd() {
    Log.e(TAG, "loadInterstitialAd")
    if (mInterstitialAd != null) {
      if (!mInterstitialAd!!.isLoading && !mInterstitialAd!!.isLoaded) {
        mInterstitialAd!!.loadAd(AdRequest.Builder().build())
      }
      return
    }
    mInterstitialAd = InterstitialAd(this)
    mInterstitialAd!!.adUnitId = getString(R.string.interstitial_full_screen)
    mInterstitialAd!!.loadAd(AdRequest.Builder().build())
    mInterstitialAd!!.adListener = adListener
  }

  private fun loadRewardedVideoAd() {
    Log.e(TAG, "loadRewardedVideoAd")
    if (mRewardedVideoAd != null) {
      if (!mRewardedVideoAd!!.isLoaded) {
        mRewardedVideoAd!!.loadAd(getString(R.string.rewarded_video), AdRequest.Builder().build())
      }
      return
    }
    mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)
    mRewardedVideoAd!!.rewardedVideoAdListener = rewardedVideoAdListener
    mRewardedVideoAd!!.loadAd(getString(R.string.rewarded_video), AdRequest.Builder().build())
  }

  companion object {
    private const val TAG = "MainActivity"
  }
}
