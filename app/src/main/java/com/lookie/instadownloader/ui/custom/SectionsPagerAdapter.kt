package com.lookie.instadownloader.ui.custom

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.lookie.instadownloader.data.room.entity.Post
import com.lookie.instadownloader.ui.postdetails.MediaFragment
import java.util.ArrayList

/**
 * @author Phung Nguyen on 2020-01-13 00:21
 **/
class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

  val mList: MutableList<Post> = ArrayList()

  fun addPost(post: Post) {
    mList.add(post)
  }

  override fun getItem(position: Int): Fragment {
    return MediaFragment.newInstance(
      mList[position]
    )
  }

  override fun getCount(): Int {
    return mList.size
  }
}