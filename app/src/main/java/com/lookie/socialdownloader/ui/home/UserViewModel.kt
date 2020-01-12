package com.lookie.socialdownloader.ui.home

import com.lookie.socialdownloader.data.room.entity.User

class UserViewModel(user: User) {
  var id = user.id
  var profilePicUrl = user.profilePicUrl
  var username = user.username
  var fullName = user.fullName
  var createAt = user.createAt
}