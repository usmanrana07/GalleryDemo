package com.gallerydemo.data.local.models

data class MediaItem(
    val id: Int,
    val path: String,
    val width: Int,
    val height: Int,
    val size: Long,
    val mimeType: String
) {
    val isVideo: Boolean = mimeType.startsWith("video", true)
}