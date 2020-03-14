package com.rdstory.apputils

import android.app.Application
import android.content.Context

open class AppContextApplication : Application() {
    override fun onCreate() {
        AppContext.init(this)
        super.onCreate()
    }
}

object AppContext {
    lateinit var application: Application
        private set
    lateinit var context: Context
        private set

    fun init(appApplication: AppContextApplication) {
        this.application = appApplication
        this.context = appApplication.applicationContext
    }
}