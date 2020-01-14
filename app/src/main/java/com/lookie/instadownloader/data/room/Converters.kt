/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lookie.instadownloader.data.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lookie.instadownloader.data.remote.model.ChildrenModel
import com.lookie.instadownloader.data.remote.model.OwnerModel

/**
 * Type converters to allow Room to reference complex data types.
 */
class Converters {

  @TypeConverter
  fun stringToChildren(s: String?): ChildrenModel? {
    return Gson().fromJson(s, object : TypeToken<ChildrenModel?>() {}.type)
  }

  @TypeConverter
  fun childrenToString(childrenModel: ChildrenModel?): String? {
    return Gson().toJson(childrenModel)
  }

  @TypeConverter
  fun stringToOwner(s: String?): OwnerModel? {
    return Gson().fromJson(s, object : TypeToken<OwnerModel?>() {}.type)
  }

  @TypeConverter
  fun ownerToString(ownerModel: OwnerModel?): String? {
    return Gson().toJson(ownerModel)
  }
}