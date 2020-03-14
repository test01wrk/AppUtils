package com.rdstory.apputils

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.math.max

object TaskManager {
    private val EXECUTORS = ThreadPoolExecutor(2,
        max(8, 2 * Runtime.getRuntime().availableProcessors()),
        60L, TimeUnit.SECONDS, LinkedBlockingQueue())
    private val mainHandler = Handler(Looper.getMainLooper())
    private val workerHandler: Handler by lazy {
        Handler(HandlerThread("worker-handler").apply { start() }.looper)
    }

    fun enqueue(action: () -> Unit) {
        workerHandler.post(action)
    }

    fun execute(action: () -> Unit) {
        EXECUTORS.execute(action)
    }

    fun executeMain(action: () -> Unit) {
        mainHandler.post(action)
    }
}