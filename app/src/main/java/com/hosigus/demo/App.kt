package com.hosigus.demo

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

import com.hosigus.tools.utils.setContext

/**
 * Created by 某只机智 on 2018/6/8.
 */
class App : Application() {
    lateinit var context: Context
        private set

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this
        context = applicationContext
        setContext(context)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var INSTANCE:App
            private set
    }
}