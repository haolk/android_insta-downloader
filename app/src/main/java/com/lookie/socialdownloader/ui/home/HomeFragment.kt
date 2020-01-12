package com.lookie.socialdownloader.ui.home

import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.lookie.socialdownloader.R
import com.lookie.socialdownloader.data.remote.model.*
import com.lookie.socialdownloader.data.rest.ApiGenerator
import com.lookie.socialdownloader.data.rest.ApiMain
import com.lookie.socialdownloader.data.room.entity.Post
import com.lookie.socialdownloader.data.room.entity.User
import com.lookie.socialdownloader.databinding.FragmentHomeBinding
import com.lookie.socialdownloader.ui.download.PostListViewModel
import com.lookie.socialdownloader.ui.main.MainActivity
import com.lookie.socialdownloader.utilities.FileUtils.createImageFile
import com.lookie.socialdownloader.utilities.FileUtils.scanFile
import com.lookie.socialdownloader.utilities.FileUtils.writeResponseBodyToDisk
import com.lookie.socialdownloader.utilities.InjectorUtils
import com.lookie.socialdownloader.utilities.SystemUtils
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class HomeFragment : Fragment(), UserAdapter.OnItemClickListener {

  companion object {
    private const val TAG = "HomeFragment"
  }

  private var mLastPost: Post? = null

  private val viewModelPost: PostListViewModel by viewModels {
    InjectorUtils.providePostListViewModelFactory(requireContext())
  }

  private val viewModelUser: UserListViewModel by viewModels {
    InjectorUtils.provideUserListViewModelFactory(requireContext())
  }

  private var mBinding: FragmentHomeBinding? = null

  private val mGson = Gson()

  private var mPost: ShortMediaModel? = null

  private var mCount = 0

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

    viewModelPost.lastPost.observe(viewLifecycleOwner, Observer<Post> { result ->
      if (result != null) {
        mLastPost = result
        val media = ShortMediaModel()
        media.id = result.id
        media.shortcode = result.shortcode
        media.displayUrl = result.displayUrl
        media.videoUrl = result.videoUrl
        media.children = result.children
        media.caption = result.caption
        media.owner = result.owner
        media.text = result.text
        binViewData(media)
        mBinding!!.hasLatest = true
      }
    })

    mBinding!!.imgMenu.setOnClickListener {
      showPostMenu(mBinding!!.imgMenu, mLastPost)
    }

    mBinding!!.btnDownload.setOnClickListener {
      val link = edt_link.text.toString()
      if (!TextUtils.isEmpty(link)) {
        if (isInstaLink(link)) {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val main = (activity as MainActivity)
            if (main.isReadStoragePermissionGranted && main.isWriteStoragePermissionGranted) {
              downloadMediaData(link)
            } else {
              Toast.makeText(activity, "Permission is revoked", Toast.LENGTH_SHORT).show()
            }
          } else {
            downloadMediaData(link)
          }
        } else {
          Toast.makeText(activity, "This is not an Instagram link", Toast.LENGTH_SHORT).show()
        }
      } else {
        Toast.makeText(activity, "Instagram link not found", Toast.LENGTH_SHORT).show()
      }
    }

    mBinding!!.btnPasteLink.setOnClickListener {
      val clipboard = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
      val item = clipboard.primaryClip!!.getItemAt(0)
      val pasteData = item.text
      if (pasteData != null) {
        mBinding!!.edtLink.setText(pasteData.toString())
      } else {
        Toast.makeText(activity, "Instagram link not found", Toast.LENGTH_SHORT).show()
      }
    }

    val adapter = UserAdapter(this)
    mBinding!!.userList.adapter = adapter

    viewModelUser.users.observe(viewLifecycleOwner, Observer<List<User>> { users ->
      println("users: " + users.size)
      mBinding!!.hasUsers = !users.isNullOrEmpty()
      adapter.submitList(users)
    })

    return mBinding!!.root
  }

  private fun isInstaLink(link: String): Boolean {
    return link.contains("instagram.com")
  }

  private fun downloadMediaData(link: String) {

    if (!SystemUtils.isNetworkAvailable(context)) {
      Toast.makeText(activity, R.string.no_internet_connection, Toast.LENGTH_SHORT).show()
      return
    }

    val shortCode = link.replace("https://www.instagram.com/p/", "").split("/")[0]

    mBinding!!.progress.visibility = View.VISIBLE
    mBinding!!.hasLatest = false
    mBinding!!.hasLatest = false

    mBinding!!.btnDownload.isEnabled = false

    ApiGenerator.instance!!.createService(ApiMain::class.java)
      .getPost(shortCode)!!
      .enqueue(object :
        Callback<ResponseBody?> {

        override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
          try {
            val instaModel = mGson.fromJson(response.body()!!.string(), InstaModel::class.java)
            mPost = instaModel.grapql!!.shortMedia
            showMedia(mPost)
            insertData(mPost)
          } catch (e: Exception) {
            Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
            mBinding!!.progress.visibility = View.GONE
            mBinding!!.hasLatest = false
            mBinding!!.btnDownload.isEnabled = true
          }
        }

        override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
          Log.e(TAG, "onFailure")
          mBinding!!.progress.visibility = View.GONE
          mBinding!!.hasLatest = false
          mBinding!!.btnDownload.isEnabled = true
        }
      })
  }

  private fun insertData(mPost: ShortMediaModel?) {
    val thread = Thread(Runnable {
      try {
        viewModelPost.insertPost(mPost)
        viewModelUser.insertUser(mPost!!.owner)
      } catch (e: Exception) {
        e.printStackTrace()
      }
    })
    thread.start()
  }

  private fun showMedia(media: ShortMediaModel?) {
    println("showMedia")

    binViewData(media)

    val multiMedia = media!!.children!!.edges!!.isNotEmpty()
    if (multiMedia) {
      mCount = media.children!!.edges!!.size
      downloadMultiMedia(media)
    } else {
      mCount = 1
      downloadSingleMedia(media)
    }
  }

  private fun binViewData(media: ShortMediaModel?) {
    println("binViewData")

    Glide.with(activity!!).load(media!!.displayUrl).into(mBinding!!.imgMedia)
    Glide.with(activity!!).load(media.owner!!.profilePicUrl).into(mBinding!!.imgAvatar)

    mBinding!!.textUsername.text = media.owner!!.username

    if (media.caption!!.edges != null && media.caption!!.edges!!.isNotEmpty()) {
      mBinding!!.textDesc.text = media.caption!!.edges!![0].note!!.text
    }

    mBinding!!.imgVideo.visibility = if (media.isVideo) View.VISIBLE else View.GONE

    val isMultiMedia = media.children!!.edges!!.isNotEmpty()
    mBinding!!.imgMulti.visibility = if (isMultiMedia) View.VISIBLE else View.GONE
  }

  private fun downloadMultiMedia(media: ShortMediaModel?) {
    println("downloadMultiMedia")

    for (edge: EdgeModel in media!!.children!!.edges!!) {
      downloadSingleMedia(edge.note)
    }
  }

  private fun downloadSingleMedia(media: ShortMediaModel?) {
    println("downloadSingleMedia")

    val prefix = if (media!!.isVideo) ".mp4" else ".jpg"
    val mediaUrl = if (media.isVideo) media.videoUrl!! else media.displayUrl!!
    val file = createImageFile(context!!, "instagram_${media.shortcode}${prefix}")
    downloadFile(mediaUrl, file)
  }

  private fun downloadFile(mediaUrl: String, file: File) {
    println("downloadFile $mediaUrl")

    mCount--

//    if (file.exists()) {
//      Toast.makeText(context, "${file.absolutePath} exists", Toast.LENGTH_SHORT).show()
//      if (mCount <= 0) {
//        mBinding!!.progress.visibility = View.GONE
//        mBinding!!.hasLatest = false
//      }
//      return
//    }

    ApiGenerator.instance!!.createService(ApiMain::class.java).downloadFile(mediaUrl)!!
      .enqueue(object : Callback<ResponseBody?> {

        override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
          mBinding!!.progress.visibility = View.GONE
          mBinding!!.btnDownload.isEnabled = true
          if (response.isSuccessful) {
            val writtenToDisk = writeResponseBodyToDisk(response.body()!!, file)
            if (writtenToDisk) {
              Log.e(TAG, "File: ${file.absolutePath}")
              if (mCount <= 0) {
                Toast.makeText(context, "Download successfully", Toast.LENGTH_SHORT).show()
                mBinding!!.hasLatest = true
                val isMultiMedia = mPost!!.children!!.edges!!.isNotEmpty()
                mBinding!!.imgMulti.visibility = if (isMultiMedia) View.VISIBLE else View.GONE
                scanFile(context, file)
              }
            } else {
              Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
              mBinding!!.hasLatest = false
            }
          } else {
            Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
            mBinding!!.hasLatest = false
          }
        }

        override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
          mBinding!!.progress.visibility = View.GONE
          mBinding!!.hasLatest = false
        }
      })
  }

  private fun showPostMenu(v: View, post: Post?) {
    val popup = PopupMenu(context!!, v)
    popup.menuInflater.inflate(R.menu.menu_item_post, popup.menu)
    popup.setOnMenuItemClickListener { item ->
      when (item.itemId) {
        R.id.view_on_insta -> {
          SystemUtils.openInstagram(activity, post!!.shortcode)
        }
        R.id.repost_for_insta -> {
          SystemUtils.repostInsta(activity, post)
        }
        R.id.share -> {
          SystemUtils.shareLink(activity, post)
        }
        R.id.delete -> {
          viewModelPost.deletePost(post)
        }
        R.id.copy_link -> {
          SystemUtils.copyText(activity, "https://www.instagram.com/p/${post!!.shortcode}/")
        }
        R.id.copy_caption -> {
          if (post!!.caption.edges!!.isNotEmpty()) {
            val caption = post.caption.edges!![0].note!!.text
            SystemUtils.copyText(activity, caption)
          } else {
            Toast.makeText(activity, "Caption not found", Toast.LENGTH_SHORT).show()
          }
        }
      }
      true
    }

    val menuHelper = MenuPopupHelper(context!!, (popup.menu as MenuBuilder), v)
    menuHelper.setForceShowIcon(true)
    menuHelper.gravity = Gravity.END
    menuHelper.show()
  }

  override fun onItemClick(view: View?, user: User?, position: Int) {
    SystemUtils.openProfileInstagram(activity, user!!.username)
  }
}