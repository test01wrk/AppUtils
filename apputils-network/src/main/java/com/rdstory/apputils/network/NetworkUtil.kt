package com.rdstory.apputils.network

import android.util.Log
import androidx.annotation.WorkerThread
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.rdstory.apputils.AppContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.Exception

object NetworkUtil {
    private const val DEBUG = true
    private val TAG = NetworkUtil::class.java.simpleName
    private const val UA_MAC_CHROME = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36"
    private val okClient: OkHttpClient = OkHttpClient.Builder()
        .cookieJar(PersistentCookieJar(SetCookieCache(),
            SharedPrefsCookiePersistor(AppContext.application)))
        .addNetworkInterceptor(HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
//                Log.d(TAG, "okhttplog: message=$message")
                loggers.forEach { it.log(message) }
            }
        }).apply {
            level = if (BuildConfig.DEBUG || DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.BASIC
        })
        .build()
    private val loggers: MutableSet<HttpLoggingInterceptor.Logger> = Collections.newSetFromMap(ConcurrentHashMap())

    fun addHttpLogger(logger: HttpLoggingInterceptor.Logger) {
        loggers.add(logger)
    }

    fun removeHttpLogger(logger: HttpLoggingInterceptor.Logger) {
        loggers.remove(logger)
    }

    private fun isHttpUrl(url: String?): Boolean {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"))
    }

    private fun commonHeaders(url: String): Headers {
        return Headers.Builder()
            .add("User-Agent", UA_MAC_CHROME)
            .build()
    }

    @WorkerThread
    fun get(url: String): NetworkResult<String?> {
        if (!isHttpUrl(url)) return NetworkResult.failure(IllegalArgumentException("invalid url: $url"))
        val request = Request.Builder().headers(commonHeaders(url)).url(url).get().build()
        return try {
            NetworkResult.success(okClient.newCall(request).execute().takeIf { it.isSuccessful }?.body?.string())
        } catch (e: Throwable) {
            Log.e(TAG, "get[$url] failed: $e", e)
            NetworkResult.failure(e)
        }
    }

    @WorkerThread
    fun post(url: String, json: String): NetworkResult<String?> {
        if (!isHttpUrl(url)) return NetworkResult.failure(IllegalArgumentException("invalid url: $url"))
        val postBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder().headers(commonHeaders(url)).url(url).post(postBody).build()
        return try {
            NetworkResult.success(okClient.newCall(request).execute().takeIf { it.isSuccessful }?.body?.string())
        } catch (e: Exception) {
            Log.e(TAG, "post[$url] failed: $e", e)
            NetworkResult.failure(e)
        }
    }

    @WorkerThread
    fun post(url: String, data: Map<String, String>): NetworkResult<String?> {
        if (!isHttpUrl(url)) return NetworkResult.failure(IllegalArgumentException("invalid url: $url"))
        val postBody = FormBody.Builder().also { body -> data.forEach { body.add(it.key, it.value) } }.build()
        val request = Request.Builder().headers(commonHeaders(url)).url(url).post(postBody).build()
        return try {
            NetworkResult.success(okClient.newCall(request).execute().takeIf { it.isSuccessful }?.body?.string())
        } catch (e: Exception) {
            Log.e(TAG, "post[$url] failed: $e", e)
            NetworkResult.failure(e)
        }
    }
}