package com.gallerydemo.data.local.models

import kotlin.properties.Delegates

data class MediaItem(
    val id: Int,
    val path: String,
    val width: Int,
    val height: Int,
    val size: Long
) {
    var isVideo by Delegates.notNull<Boolean>()
    var mimeType: String? = null
        set(value) {
            isVideo = value?.startsWith("video", true) ?: false
            field = value
        }

}