package com.example

import android.app.Application

class MyApplication : Application() {
    companion object {
        init {
            System.setProperty("poi.useLambdaMetafactory", "false")
            System.setProperty("org.apache.poi.useLambdaMetafactory", "false")
        }
    }

    override fun onCreate() {
        super.onCreate()
        System.setProperty("poi.useLambdaMetafactory", "false")
        System.setProperty("org.apache.poi.useLambdaMetafactory", "false")
    }
}
