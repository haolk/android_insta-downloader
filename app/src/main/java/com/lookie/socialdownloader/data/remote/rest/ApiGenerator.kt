package com.lookie.socialdownloader.data.rest

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiGenerator private constructor() {

  private val mRetrofit: Retrofit

  private fun createRetrofit(): Retrofit {

    val builder = OkHttpClient.Builder()
    builder
      .connectTimeout(60, TimeUnit.SECONDS)
      .writeTimeout(60, TimeUnit.SECONDS)
      .readTimeout(60, TimeUnit.SECONDS)

    val headerAuthorizationInterceptor = object : Interceptor {
      override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val headers = request.headers.newBuilder()
//          .add("Cache-Control", "no-cache")
//          .add("Content-Language", "en")
//          .add(
//            "User-Agent",
//            "Instagram 9.5.2 (iPhone7,2; iPhone OS 9_3_3; en_US; en-US; scale=2.00; 750x1334) AppleWebKit/420+"
//          )
//          .add("Vary", "Cookie, Accept-Language")
//          .add("Content-Type", "application/json")
//          .add("Cookie", "ds_user_id=683101976; sessionid=683101976%3AmBlA6iboJ2UuKM%3A22;")
          .build()
        request = request.newBuilder().headers(headers).build()
        return chain.proceed(request)
      }
    }
    builder.addInterceptor(headerAuthorizationInterceptor)
    builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

    // builder.addInterceptor(ApiCustomInterceptor())

    return Retrofit.Builder()
      .baseUrl("https://www.instagram.com/p/")
      .addConverterFactory(GsonConverterFactory.create())
      .client(builder.build())
      .build()
  }

  fun <S> createService(serviceClass: Class<S>?): S {
    return mRetrofit.create(serviceClass!!)
  }

  companion object {
    private var mInstance: ApiGenerator? =
      ApiGenerator()
    @JvmStatic
    @get:Synchronized
    val instance: ApiGenerator?
      get() {
        if (mInstance == null) {
          mInstance =
            ApiGenerator()
        }
        return mInstance
      }
  }

  init {
    mRetrofit = createRetrofit()
  }
}