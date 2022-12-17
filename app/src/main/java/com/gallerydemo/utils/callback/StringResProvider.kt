package com.gallerydemo.utils.callback

import androidx.annotation.StringRes

interface StringResProvider {
    fun getString(@StringRes resId: Int): String
}