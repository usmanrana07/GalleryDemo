package com.gallerydemo.ui.main.media.adapter

import com.gallerydemo.data.local.models.MediaItem

class MediaListItemViewModel(mediaItem: MediaItem, private val listener: () -> Unit) {
    val thumbnail: String = mediaItem.path

    fun onItemClick() {
        listener()
    }
}