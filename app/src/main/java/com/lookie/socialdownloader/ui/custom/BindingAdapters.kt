package com.lookie.socialdownloader.ui.custom

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import de.hdodenhof.circleimageview.CircleImageView

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
  view.visibility = if (isGone) {
    View.GONE
  } else {
    View.VISIBLE
  }
}

@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
  if (!imageUrl.isNullOrEmpty()) {
    Glide.with(view.context)
      .load(imageUrl)
      .transition(DrawableTransitionOptions.withCrossFade())
      .into(view)
  }
}

@BindingAdapter("circleImageFromUrl")
fun bindCircleImageFromUrl(view: CircleImageView, imageUrl: String?) {
  if (!imageUrl.isNullOrEmpty()) {
    Glide.with(view.context)
      .load(imageUrl)
      .transition(DrawableTransitionOptions.withCrossFade())
      .into(view)
  }
}
