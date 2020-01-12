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
import com.lookie.socialdownloader.ui.download.PostListViewModel
import com.lookie.socialdownloader.utilities.EXTRA_POST
import com.lookie.socialdownloader.utilities.InjectorUtils
import com.lookie.socialdownloader.utilities.SystemUtils
import com.lookie.socialdownloader.utilities.SystemUtils.setStatusBarColor

class PostDetailsActivity : AppCompatActivity() {

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

    val pagerAdapter = SectionsPagerAdapter(supportFragmentManager)

    if (intent != null && intent.extras != null) {
      val post = intent.extras!!.getParcelable<Post>(EXTRA_POST)
      mCurrentPost = post
      println("PostDetailsActivity: $post")

      val multiMedia = post!!.children.edges!!.isNotEmpty()
      println("multiMedia: $multiMedia")

      if (multiMedia) {
        for (edge: EdgeModel in post.children.edges!!) {
          pagerAdapter.addPost(
            Post(
              edge.note!!.id!!, edge.note!!.shortcode!!,
              edge.note!!.displayUrl!!, edge.note!!.videoUrl!!, edge.note!!.isVideo,
              edge.note!!.text!!, post.createAt,
              edge.note!!.children!!, edge.note!!.caption!!, edge.note!!.owner!!
            )
          )
        }
      } else {
        pagerAdapter.addPost(post)
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
      SystemUtils.openInstagram(this, mCurrentPost!!.shortcode)
    }
    binding.imgRepost.setOnClickListener {
      SystemUtils.repostInsta(this, mCurrentPost)
    }
    binding.imgShare.setOnClickListener {
      SystemUtils.shareLink(this, mCurrentPost)
    }
    binding.imgDelete.setOnClickListener {
      viewModelPost.deletePost(mCurrentPost)
      finish()
    }
    binding.imgCopyLink.setOnClickListener {
      SystemUtils.copyText(
        this,
        "https://www.instagram.com/p/${mCurrentPost!!.shortcode}/",
        R.string.copied_link_to_clipboard
      )
    }
    binding.imgCopyCaption.setOnClickListener {
      if (mCurrentPost!!.caption.edges!!.isNotEmpty()) {
        val caption = mCurrentPost!!.caption.edges!![0].note!!.text
        SystemUtils.copyText(this, caption, R.string.copied_caption_to_clipboard)
      } else {
        Toast.makeText(this, R.string.caption_not_found, Toast.LENGTH_SHORT).show()
      }
    }
  }
}