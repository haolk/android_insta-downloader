package com.lookie.socialdownloader.ui.postdetails

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.lookie.socialdownloader.R
import com.lookie.socialdownloader.data.room.entity.Post
import com.lookie.socialdownloader.databinding.FragmentMediaBinding
import com.lookie.socialdownloader.utilities.EXTRA_POST
import com.lookie.socialdownloader.utilities.UniversalVideoView

/**
 * @author Phung Nguyen on 2020-01-13 00:51
 **/
class MediaFragment : Fragment() {

  private var mPost: Post? = null
  private var binding: FragmentMediaBinding? = null

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_media, container, false)

    if (arguments != null && arguments!!.containsKey(ARG_POST)) {
      mPost = arguments!!.getParcelable(EXTRA_POST)
      Glide.with(this).load(mPost!!.displayUrl).into(binding!!.photoView)
    }

    binding!!.videoView.setMediaController(binding!!.mediaController)

    binding!!.videoView.setVideoViewCallback(object : UniversalVideoView.VideoViewCallback {

      override fun onScaleChange(isFullscreen: Boolean) {
      }

      override fun onPause(mediaPlayer: MediaPlayer) {
        if (binding!!.mediaController.isShowing) {
          binding!!.mediaController.visibility = View.GONE
        }
      }

      override fun onStart(mediaPlayer: MediaPlayer) {
        if (!binding!!.mediaController.isShowing) {
          binding!!.mediaController.visibility = View.VISIBLE
        }
      }

      override fun onBufferingStart(mediaPlayer: MediaPlayer) {
      }

      override fun onBufferingEnd(mediaPlayer: MediaPlayer) {
      }
    })

    binding!!.videoView.setVideoPath(mPost!!.videoUrl)

    if (mPost!!.isVideo) {
      binding!!.videoLayout.visibility = View.VISIBLE
      binding!!.photoView.visibility = View.GONE
      binding!!.videoView.start()
    } else {
      binding!!.videoLayout.visibility = View.GONE
      binding!!.photoView.visibility = View.VISIBLE
    }

    return binding!!.root
  }

  override fun onPause() {
    super.onPause()
    binding!!.videoView.stopPlayback()
    binding!!.videoView.resume()
  }

  companion object {

    private const val ARG_POST = "post"

    fun newInstance(post: Post): MediaFragment {
      val fragment = MediaFragment()
      val args = Bundle()
      args.putParcelable(ARG_POST, post)
      fragment.arguments = args
      return fragment
    }
  }
}