package com.lookie.instadownloader.ui.home

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.gms.ads.*
import com.google.gson.Gson
import com.lookie.instadownloader.R
import com.lookie.instadownloader.base.BaseActivity
import com.lookie.instadownloader.data.remote.model.EdgeModel
import com.lookie.instadownloader.data.remote.model.InstaModel
import com.lookie.instadownloader.data.remote.model.ShortMediaModel
import com.lookie.instadownloader.data.rest.ApiGenerator
import com.lookie.instadownloader.data.rest.ApiMain
import com.lookie.instadownloader.data.room.entity.Post
import com.lookie.instadownloader.data.room.entity.User
import com.lookie.instadownloader.databinding.FragmentHomeBinding
import com.lookie.instadownloader.ui.custom.InterstitialAdCallback
import com.lookie.instadownloader.ui.download.PostListViewModel
import com.lookie.instadownloader.ui.main.MainActivity
import com.lookie.instadownloader.ui.postdetails.PostDetailsActivity
import com.lookie.instadownloader.ui.settings.SettingsActivity
import com.lookie.instadownloader.utilities.EXTRA_POST
import com.lookie.instadownloader.utilities.FileUtils.createImageFile
import com.lookie.instadownloader.utilities.FileUtils.scanFile
import com.lookie.instadownloader.utilities.FileUtils.writeResponseBodyToDisk
import com.lookie.instadownloader.utilities.InjectorUtils
import com.lookie.instadownloader.utilities.SharedPrefUtils
import com.lookie.instadownloader.utilities.SystemUtils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class HomeFragment : Fragment(), UserAdapter.OnItemClickListener {

  companion object {

    private const val TAG = "HomeFragment"
    private const val LINK = "link"

    fun newInstance(link: String): HomeFragment {
      val fragment = HomeFragment()
      val args = Bundle()
      args.putString(LINK, link)
      fragment.arguments = args
      return fragment
    }
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

    setHasOptionsMenu(true)

    if (SharedPrefUtils.instance!!.premium!!) {
      mBinding!!.adView.visibility = View.GONE
    } else {
      mBinding!!.adView.visibility = View.VISIBLE
      mBinding!!.adView.loadAd(AdRequest.Builder().build())
    }

    viewModelPost.lastPost.observe(viewLifecycleOwner, Observer<Post> { result ->
      if (result != null) {
        mLastPost = result
        binViewData(result)
        mBinding!!.hasLatest = true
      } else {
        mBinding!!.hasLatest = false
      }
    })

    mBinding!!.imgMenu.setOnClickListener {
      showPostMenu(mBinding!!.imgMenu, mLastPost)
    }

    mBinding!!.cardMedia.setOnClickListener {
      val intent = Intent(activity, PostDetailsActivity::class.java)
      intent.putExtra(EXTRA_POST, mLastPost as Parcelable)
      startActivity(intent)
    }

    mBinding!!.btnDownload.setOnClickListener {
      val link = mBinding!!.edtLink.text.toString()
      if (!TextUtils.isEmpty(link)) {
        (activity as MainActivity).showInterstitialAds(object : InterstitialAdCallback {
          override fun onAdClosed() {
            download(link)
          }
        })
      } else {
        Toast.makeText(activity, R.string.insta_link_not_found, Toast.LENGTH_SHORT).show()
      }
    }

    mBinding!!.btnPasteLink.setOnClickListener {
      val clipboard = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
      val item = clipboard.primaryClip!!.getItemAt(0)
      val pasteData = item.text
      if (pasteData != null) {
        mBinding!!.edtLink.setText(pasteData.toString())
      } else {
        Toast.makeText(activity, R.string.insta_link_not_found, Toast.LENGTH_SHORT).show()
      }
    }

    val adapter = UserAdapter(this)
    mBinding!!.userList.adapter = adapter

    viewModelUser.users.observe(viewLifecycleOwner, Observer<List<User>> { users ->
      mBinding!!.hasUsers = !users.isNullOrEmpty()
      adapter.submitList(users)
    })

    val link = arguments!!.getString(LINK, "")

    if (!TextUtils.isEmpty(link)) {
      mBinding!!.edtLink.setText(link)
      (activity as MainActivity).showInterstitialAds(object : InterstitialAdCallback {
        override fun onAdClosed() {
          download(link)
        }
      })
    }

    return mBinding!!.root
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    if (SharedPrefUtils.instance!!.premium!!) {
      inflater.inflate(R.menu.menu_home_premium, menu)
    } else {
      inflater.inflate(R.menu.menu_home, menu)
    }
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return (when (item.itemId) {
      R.id.action_remove_ads -> {
        (activity as MainActivity).doUpgrade()
        true
      }
      R.id.action_open_insta -> {
        SystemUtils.openInstagram(context)
        true
      }
      R.id.action_share_app -> {
        SystemUtils.shareApp(context)
        true
      }
      R.id.action_family_app -> {
        SystemUtils.openMoreApps(activity)
        true
      }
      R.id.action_settings -> {
        val intent = Intent(activity, SettingsActivity::class.java)
        startActivity(intent)
        true
      }
      else ->
        super.onOptionsItemSelected(item)
    })
  }

  private fun download(link: String) {
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
      Toast.makeText(activity, R.string.this_is_not_an_insta_link, Toast.LENGTH_SHORT).show()
    }
  }

  private fun isInstaLink(link: String): Boolean {
    return link.contains("instagram.com")
  }

  private fun downloadMediaData(link: String) {

    if (!SystemUtils.isNetworkAvailable(context)) {
      Toast.makeText(activity, R.string.no_internet_connection, Toast.LENGTH_SHORT).show()
      return
    }

    val shortCode = link
      .replace("https://www.instagram.com/p/", "")
      .split("/")[0]

    mBinding!!.progress.visibility = View.VISIBLE
    mBinding!!.btnDownload.isEnabled = false

    ApiGenerator.instance!!.createService(ApiMain::class.java)
      .getPost(shortCode)!!
      .enqueue(object :
        Callback<ResponseBody?> {

        override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
          try {
            val instaModel = mGson.fromJson(response.body()!!.string(), InstaModel::class.java)
            mPost = instaModel.grapql!!.shortMedia

            // insert database
            insertData(mPost)

            // download all media
            if (mPost!!.isMultiMedia()) {
              mCount = mPost!!.children!!.edges!!.size
              downloadMultiMedia(mPost)
            } else {
              mCount = 1
              downloadSingleMedia(mPost)
            }

          } catch (e: Exception) {
            Toast.makeText(context, R.string.post_download_failed, Toast.LENGTH_SHORT).show()
            mBinding!!.progress.visibility = View.GONE
            mBinding!!.btnDownload.isEnabled = true
          }
        }

        override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
          Log.e(TAG, "onFailure")
          mBinding!!.progress.visibility = View.GONE
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

  private fun binViewData(post: Post?) {

    Glide.with(activity!!).load(post!!.displayUrl).into(mBinding!!.imgMedia)

    Glide.with(activity!!).load(post.owner.profilePicUrl).into(mBinding!!.imgAvatar)

    mBinding!!.textUsername.text = post.owner.username

    if (post.hasCaptionText()) {
      mBinding!!.textDesc.text = post.getCaptionText()
    }

    mBinding!!.imgVideo.visibility = if (post.isVideo) View.VISIBLE else View.GONE

    mBinding!!.imgMulti.visibility = if (post.isMultiMedia()) View.VISIBLE else View.GONE
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

    mCount--

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
                Toast.makeText(context, R.string.post_download_successfully, Toast.LENGTH_SHORT)
                  .show()
                mBinding!!.imgMulti.visibility =
                  if (mPost!!.isMultiMedia()) View.VISIBLE else View.GONE
                scanFile(context, file)
              }
            } else {
              Toast.makeText(context, R.string.post_download_failed, Toast.LENGTH_SHORT).show()
            }
          } else {
            Toast.makeText(context, R.string.post_download_failed, Toast.LENGTH_SHORT).show()
          }
        }

        override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
          mBinding!!.progress.visibility = View.GONE
        }
      })
  }

  private fun showPostMenu(v: View, post: Post?) {
    val popup = PopupMenu(context!!, v)
    popup.menuInflater.inflate(R.menu.menu_item_post, popup.menu)
    popup.setOnMenuItemClickListener { item ->
      when (item.itemId) {
        R.id.view_on_insta -> {
          (activity as MainActivity).showInterstitialAds(object : InterstitialAdCallback {
            override fun onAdClosed() {
              SystemUtils.openInstagram(activity, post!!.shortcode)
            }
          })
        }
        R.id.repost_for_insta -> {
          (activity as MainActivity).showInterstitialAds(object : InterstitialAdCallback {
            override fun onAdClosed() {
              SystemUtils.repostInsta(activity, post)
            }
          })
        }
        R.id.share_media -> {
          (activity as MainActivity).showInterstitialAds(object : InterstitialAdCallback {
            override fun onAdClosed() {
              SystemUtils.shareLocalMedia(activity, post)
            }
          })
        }
        R.id.share_link -> {
          (activity as MainActivity).showInterstitialAds(object : InterstitialAdCallback {
            override fun onAdClosed() {
              SystemUtils.shareLink(activity, post)
            }
          })
        }
        R.id.delete -> {
          viewModelPost.deletePost(post)
        }
        R.id.copy_link -> {
          (activity as MainActivity).showInterstitialAds(object : InterstitialAdCallback {
            override fun onAdClosed() {
              SystemUtils.copyText(
                activity,
                "https://www.instagram.com/p/${post!!.shortcode}/",
                R.string.copied_link_to_clipboard
              )
            }
          })
        }
        R.id.copy_caption -> {
          (activity as MainActivity).showInterstitialAds(object : InterstitialAdCallback {
            override fun onAdClosed() {
              if (post!!.hasCaptionText()) {
                val caption = post.getCaptionText()
                SystemUtils.copyText(activity, caption, R.string.copied_caption_to_clipboard)
              } else {
                Toast.makeText(activity, R.string.caption_not_found, Toast.LENGTH_SHORT).show()
              }
            }
          })
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

  fun setLink(link: String) {
    mBinding!!.edtLink.setText(link)
    if (!TextUtils.isEmpty(link)) {
      (activity as MainActivity).showInterstitialAds(object : InterstitialAdCallback {
        override fun onAdClosed() {
          download(link)
        }
      })
    } else {
      Toast.makeText(activity, R.string.insta_link_not_found, Toast.LENGTH_SHORT).show()
    }
  }
}