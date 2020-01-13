package com.lookie.socialdownloader.ui.download

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
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
import com.lookie.socialdownloader.R
import com.lookie.socialdownloader.data.room.entity.Post
import com.lookie.socialdownloader.databinding.FragmentDownloadBinding
import com.lookie.socialdownloader.ui.postdetails.PostDetailsActivity
import com.lookie.socialdownloader.utilities.EXTRA_POST
import com.lookie.socialdownloader.utilities.InjectorUtils
import com.lookie.socialdownloader.utilities.SystemUtils

class DownloadFragment : Fragment(), PostAdapter.OnItemClickListener {

  private val viewModel: PostListViewModel by viewModels {
    InjectorUtils.providePostListViewModelFactory(requireContext())
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    val binding =
      DataBindingUtil.inflate<FragmentDownloadBinding>(
        inflater,
        R.layout.fragment_download,
        container,
        false
      )

    context ?: return binding.root

    val adapter = PostAdapter(this)
    binding.postList.adapter = adapter
    binding.hasPosts = true

    subscribeUi(adapter, binding)

    return binding.root
  }

  private fun subscribeUi(adapter: PostAdapter, binding: FragmentDownloadBinding) {

    viewModel.posts.observe(viewLifecycleOwner, Observer<List<Post>> { posts ->
      binding.hasPosts = !posts.isNullOrEmpty()
      println("getPostLiveData size: ${posts.size}")
      adapter.submitList(posts)
    })
  }

  override fun onItemClick(view: View?, post: Post?, position: Int) {
    when (view!!.id) {
      R.id.card_media -> {
        val intent = Intent(activity, PostDetailsActivity::class.java)
        intent.putExtra(EXTRA_POST, post as Parcelable)
        startActivity(intent)
      }
      R.id.img_menu -> {
        showPostMenu(view, post)
      }
    }
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
          viewModel.deletePost(post)
        }
        R.id.copy_link -> {
          SystemUtils.copyText(
            activity,
            "https://www.instagram.com/p/${post!!.shortcode}/",
            R.string.copied_link_to_clipboard
          )
        }
        R.id.copy_caption -> {
          if (post!!.hasCaptionText()) {
            val caption = post.getCaptionText()
            SystemUtils.copyText(activity, caption, R.string.copied_caption_to_clipboard)
          } else {
            Toast.makeText(activity, R.string.caption_not_found, Toast.LENGTH_SHORT).show()
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
}