package com.lookie.socialdownloader.ui.download

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    subscribeUi(adapter, binding)

    return binding.root
  }

  private fun subscribeUi(adapter: PostAdapter, binding: FragmentDownloadBinding) {
    viewModel.posts.observe(viewLifecycleOwner, Observer<List<Post>> { result ->
      println("result" + result.size)
      binding.hasPosts = !result.isNullOrEmpty()
      adapter.submitList(result)
    })
  }

  override fun onItemClick(view: View?, post: Post?, position: Int) {
    when (view!!.id) {
      R.id.card_media -> {

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
          SystemUtils.copyText(activity, "https://www.instagram.com/p/${post!!.shortcode}/")
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