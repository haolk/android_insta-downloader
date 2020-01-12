package com.lookie.socialdownloader.data.rest

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import java.io.IOException

class ApiCustomInterceptor : Interceptor {

  @Throws(IOException::class)
  override fun intercept(chain: Interceptor.Chain): Response {
    val response = chain.proceed(chain.request())
    val body = response.body!!.string()
    Log.w(TAG, body)
    try {
      val baseObject = JSONObject(body)
      if (baseObject.has("graphql")) {
        val graphqlObj = baseObject.getJSONObject("graphql")
        if (graphqlObj.has("shortcode_media")) {
          val dataObj = graphqlObj.getString("shortcode_media")
          return response.newBuilder()
            .message(KEY_SUCCESSFUL)
            .body(dataObj.toResponseBody(response.body!!.contentType())).build()
        } else {
          return response.newBuilder().message(KEY_SUCCESSFUL)
            .body(body.toResponseBody(response.body!!.contentType()))
            .build()
        }
      } else {
        return response.newBuilder().message(KEY_SUCCESSFUL)
          .body(body.toResponseBody(response.body!!.contentType())).build()
      }
    } catch (e: Exception) {
      e.printStackTrace()
      return response.newBuilder().message(KEY_SUCCESSFUL)
        .body(body.toResponseBody(response.body!!.contentType()))
        .build()
    }
  }

  companion object {
    private const val TAG = "ApiCustomInterceptor"
    private const val KEY_SUCCESSFUL = "successful"
  }
}