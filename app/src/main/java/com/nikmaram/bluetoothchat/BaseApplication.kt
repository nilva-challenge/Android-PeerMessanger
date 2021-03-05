package com.nikmaram.bluetoothchat

import android.app.Application
import android.content.Context
import com.nikmaram.bluetoothchat.di.applicationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BaseApplication : Application(){

    override fun onCreate() {
        super.onCreate()

        startKoin{
            androidContext(this@BaseApplication)
            modules(applicationModule)

        }

    }



}