package com.gallerydemo.utils

import android.util.Log
import com.gallerydemo.BuildConfig

typealias OnActionCallback = () -> Unit
typealias PermissionsResultCallback = (Map<String, Boolean>) -> Unit

fun printLog(tag: String, message: String) {
    if (BuildConfig.DEBUG) {
        Log.d(tag, message)
    }
}