package com.rdstory.apputils

import android.content.Context
import android.content.SharedPreferences
import java.util.concurrent.ConcurrentHashMap

object SpUtil {
    private const val DEFAULT_SP_NAME = "app_default_config"
    private val spMap = ConcurrentHashMap<String, SharedPreferences>()

    fun getSp(spName: String = DEFAULT_SP_NAME): SharedPreferences {
         return spMap[spName] ?: let {
             val sp = AppContext.context.getSharedPreferences(spName, Context.MODE_PRIVATE)
             spMap.putIfAbsent(spName, sp) ?: sp
         }
    }

    fun getEditor(spName: String = DEFAULT_SP_NAME): SharedPreferences.Editor {
        return getSp(spName).edit()
    }
}