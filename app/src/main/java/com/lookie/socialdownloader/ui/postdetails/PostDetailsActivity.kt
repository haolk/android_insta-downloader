package com.lookie.socialdownloader.ui.postdetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.lookie.socialdownloader.R
import com.lookie.socialdownloader.data.room.entity.Post
import com.lookie.socialdownloader.databinding.ActivityPostDetailsBinding
import com.lookie.socialdownloader.ui.download.PostListViewModel
import com.lookie.socialdownloader.utilities.EXTRA_POST
import com.lookie.socialdownloader.utilities.InjectorUtils
import com.lookie.socialdownloader.utilities.SystemUtils
import com.lookie.socialdownloader.utilities.SystemUtils.setStatusBarColor

class PostDetailsActivity : AppCompatActivity() {

  private var mPost: Post? = null

  private val viewModelPost: PostListViewModel by viewModels {
    InjectorUtils.providePostListViewModelFactory(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setStatusBarColor(this, android.R.color.black)

    val binding = DataBindingUtil
      .setContentView<ActivityPostDetailsBinding>(this, R.layout.activity_post_details)

    if (intent != null && intent.extras != null) {
      mPost = intent.extras!!.getParcelable(EXTRA_POST)
      println(mPost)
      Glide.with(this).load(mPost!!.displayUrl).into(binding.photoView)
    }

    binding.back.setOnClickListener {
      finish()
    }

    binding.imgViewInsta.setOnClickListener {
      SystemUtils.openInstagram(this, mPost!!.shortcode)
    }

    binding.imgRepost.setOnClickListener {
      SystemUtils.repostInsta(this, mPost)
    }

    binding.imgShare.setOnClickListener {
      SystemUtils.shareLink(this, mPost)
    }

    binding.imgDelete.setOnClickListener {
      viewModelPost.deletePost(mPost)
      finish()
    }

    binding.imgCopyLink.setOnClickListener {
      SystemUtils.copyText(
        this,
        "https://www.instagram.com/p/${mPost!!.shortcode}/", R.string.copied_link_to_clipboard
      )
    }

    binding.imgCopyCaption.setOnClickListener {
      if (mPost!!.caption.edges!!.isNotEmpty()) {
        val caption = mPost!!.caption.edges!![0].note!!.text
        SystemUtils.copyText(this, caption, R.string.copied_caption_to_clipboard)
      } else {
        Toast.makeText(this, R.string.caption_not_found, Toast.LENGTH_SHORT).show()
      }
    }

  }
}
