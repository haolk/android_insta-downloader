package com.lookie.socialdownloader.ui.postdetails

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.lookie.socialdownloader.R
import com.lookie.socialdownloader.data.rest.ApiGenerator
import com.lookie.socialdownloader.data.rest.ApiMain
import com.lookie.socialdownloader.data.room.entity.Post
import com.lookie.socialdownloader.databinding.FragmentMediaBinding
import com.lookie.socialdownloader.utilities.EXTRA_POST
import com.lookie.socialdownloader.utilities.FileUtils
import com.lookie.socialdownloader.utilities.SystemUtils.getFile
import com.lookie.socialdownloader.utilities.UniversalVideoView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


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

    if (arguments != null && arguments!!.containsKey(ARG_POST)) {

      mPost = arguments!!.getParcelable(EXTRA_POST)

      binding!!.progress.visibility = View.VISIBLE

      val fileMedia = getFile(mPost)
      if (!fileMedia.exists()) {
        downloadFile(if (mPost!!.isVideo) mPost!!.videoUrl else mPost!!.displayUrl, fileMedia)
      } else {
        dataBindView(fileMedia)
      }
    }

    return binding!!.root
  }

  override fun onPause() {
    super.onPause()
    binding!!.videoView.stopPlayback()
    binding!!.videoView.resume()
  }

  private fun downloadFile(mediaUrl: String, file: File) {
    println("downloadFile $mediaUrl")
    println("file ${file.absolutePath}")
    ApiGenerator.instance!!.createService(ApiMain::class.java).downloadFile(mediaUrl)!!
      .enqueue(object : Callback<ResponseBody?> {

        override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
          if (response.isSuccessful) {
            val writtenToDisk = FileUtils.writeResponseBodyToDisk(response.body()!!, file)
            if (writtenToDisk) {
              FileUtils.scanFile(context, file)
              dataBindView(file)
            } else {
              Toast.makeText(context, R.string.post_download_failed, Toast.LENGTH_SHORT).show()
            }
          } else {
            Toast.makeText(context, R.string.post_download_failed, Toast.LENGTH_SHORT).show()
          }
        }

        override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
        }
      })
  }

  private fun dataBindView(file: File) {
    println("dataBindView: ${file.absolutePath}")

    binding!!.videoLayout.visibility = if (mPost!!.isVideo) View.VISIBLE else View.GONE
    binding!!.photoView.visibility = if (mPost!!.isVideo) View.GONE else View.VISIBLE
    binding!!.progress.visibility = View.GONE

    if (mPost!!.isVideo) {
      val uri = Uri.parse(file.absolutePath)
      binding!!.videoView.setVideoURI(uri)
      binding!!.videoView.start()
    } else {
      val photoUri = Uri.fromFile(file)
      Glide.with(this).load(photoUri).into(binding!!.photoView)
    }
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