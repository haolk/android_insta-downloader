package com.lookie.socialdownloader.data.rest

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url


interface ApiMain {

  @JvmSuppressWildcards
  @GET
  fun downloadFile(@Url fileUrl: String?): Call<ResponseBody?>?

  @JvmSuppressWildcards
  @GET("{id}/?__a=1")
  fun getPost(@Path("id") id: String): Call<ResponseBody?>?
}