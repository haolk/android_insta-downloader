package com.lookie.socialdownloader.utilities

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.*

class MyViewPagerAdapter(manager: FragmentManager?) : FragmentPagerAdapter(manager!!) {

  private val mFragmentList: MutableList<Fragment> = ArrayList()

  override fun getItem(position: Int): Fragment {
    return mFragmentList[position]
  }

  override fun getCount(): Int {
    return mFragmentList.size
  }

  fun addFragment(fragment: Fragment) {
    mFragmentList.add(fragment)
  }
}