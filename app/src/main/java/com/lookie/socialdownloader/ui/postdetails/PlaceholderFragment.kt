package com.lookie.socialdownloader.ui.postdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.lookie.socialdownloader.R
import com.lookie.socialdownloader.data.room.entity.Post
import com.lookie.socialdownloader.databinding.FragmentPhotoBinding
import com.lookie.socialdownloader.utilities.EXTRA_POST

/**
 * @author Phung Nguyen on 2020-01-13 00:51
 **/
class PlaceholderFragment : Fragment() {

  private var mPost: Post? = null

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    val binding = DataBindingUtil.inflate<FragmentPhotoBinding>(
      inflater,
      R.layout.fragment_photo,
      container,
      false
    )

    if (arguments != null && arguments!!.containsKey(ARG_POST)) {
      mPost = arguments!!.getParcelable(EXTRA_POST)
      Glide.with(this).load(mPost!!.displayUrl).into(binding.photoView)
    }

    println("PlaceholderFragment: $mPost")

    return binding.root
  }

  companion object {

    private const val ARG_POST = "post"

    fun newInstance(post: Post): PlaceholderFragment {
      val fragment = PlaceholderFragment()
      val args = Bundle()
      args.putParcelable(ARG_POST, post)
      fragment.arguments = args
      return fragment
    }
  }
}