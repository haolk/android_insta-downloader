package com.lookie.socialdownloader.ui.postdetails

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.lookie.socialdownloader.R
import com.lookie.socialdownloader.data.remote.model.EdgeModel
import com.lookie.socialdownloader.data.room.entity.Post
import com.lookie.socialdownloader.databinding.ActivityPostDetailsBinding
import com.lookie.socialdownloader.ui.custom.SectionsPagerAdapter
import com.lookie.socialdownloader.ui.download.PostListViewModel
import com.lookie.socialdownloader.utilities.EXTRA_POST
import com.lookie.socialdownloader.utilities.InjectorUtils
import com.lookie.socialdownloader.utilities.SystemUtils
import com.lookie.socialdownloader.utilities.SystemUtils.setStatusBarColor

class PostDetailsActivity : AppCompatActivity() {

  private var mPost: Post? = null

  private var mCurrentPost: Post? = null

  private val viewModelPost: PostListViewModel by viewModels {
    InjectorUtils.providePostListViewModelFactory(this)
  }

  @SuppressLint("SetTextI18n")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setStatusBarColor(this, android.R.color.black)

    val binding = DataBindingUtil
      .setContentView<ActivityPostDetailsBinding>(this, R.layout.activity_post_details)

    val pagerAdapter =
      SectionsPagerAdapter(
        supportFragmentManager
      )

    if (intent != null && intent.extras != null) {
      mPost = intent.extras!!.getParcelable(EXTRA_POST)
      mCurrentPost = mPost
      println("PostDetailsActivity: $mPost")

      val multiMedia = mPost!!.children.edges!!.isNotEmpty()
      println("multiMedia: $multiMedia")

      if (multiMedia) {
        for (edge: EdgeModel in mPost!!.children.edges!!) {
          pagerAdapter.addPost(
            Post(
              edge.note!!.id!!, edge.note!!.shortcode!!,
              edge.note!!.displayUrl!!, edge.note!!.videoUrl!!, edge.note!!.isVideo,
              edge.note!!.text!!, mPost!!.createAt,
              edge.note!!.children!!, edge.note!!.caption!!, edge.note!!.owner!!
            )
          )
        }
      } else {
        pagerAdapter.addPost(mPost!!)
      }
    }

    println("pagerAdapter.count: ${pagerAdapter.count}")

    binding.textPhotos.text = "1/${pagerAdapter.count}"

    binding.container.adapter = pagerAdapter

    binding.container.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

      override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        println("onPageScrolled $position")
      }

      override fun onPageSelected(position: Int) {
        println("onPageSelected $position")
        binding.textPhotos.text = "${position + 1}/${pagerAdapter.count}"
        mCurrentPost = pagerAdapter.mList[position]
      }

      override fun onPageScrollStateChanged(state: Int) {
        println("onPageScrollStateChanged $state")
      }
    })

    initOnClickListener(binding)
  }

  private fun initOnClickListener(binding: ActivityPostDetailsBinding?) {
    binding!!.back.setOnClickListener {
      finish()
    }
    binding.imgViewInsta.setOnClickListener {
      SystemUtils.openInstagram(this, mPost!!.shortcode)
    }
    binding.imgRepost.setOnClickListener {
      SystemUtils.repostInsta(this, mCurrentPost)
    }
    binding.imgShare.setOnClickListener {
      SystemUtils.shareLink(this, mPost)
    }
    binding.imgShareMedia.setOnClickListener {
      SystemUtils.shareLocalMedia(this, mPost)
    }
    binding.imgDelete.setOnClickListener {
      viewModelPost.deletePost(mPost)
      finish()
    }
    binding.imgCopyLink.setOnClickListener {
      SystemUtils.copyText(
        this,
        "https://www.instagram.com/p/${mPost!!.shortcode}/",
        R.string.copied_link_to_clipboard
      )
    }
    binding.imgCopyCaption.setOnClickListener {
      if (mPost!!.hasCaptionText()) {
        val caption = mPost!!.getCaptionText()
        SystemUtils.copyText(this, caption, R.string.copied_caption_to_clipboard)
      } else {
        Toast.makeText(this, R.string.caption_not_found, Toast.LENGTH_SHORT).show()
      }
    }
  }
}