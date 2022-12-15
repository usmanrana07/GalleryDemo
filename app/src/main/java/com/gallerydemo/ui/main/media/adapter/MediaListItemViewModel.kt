package com.gallerydemo.ui.main.media.adapter

import com.gallerydemo.data.local.models.MediaItem

class MediaListItemViewModel(mediaItem: MediaItem) {
    val thumbnail: String = mediaItem.path
}