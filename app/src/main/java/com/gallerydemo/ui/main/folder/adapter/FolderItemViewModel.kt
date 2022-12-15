package com.gallerydemo.ui.main.folder.adapter

import com.gallerydemo.data.local.models.GalleryFolder

class FolderItemViewModel(folder: GalleryFolder, private val listener: () -> Unit) {
    val thumbnail: String? = folder.mediaList.firstOrNull()?.path
    val title: String = folder.title
    val count: Int = folder.mediaList.size

    fun onItemClick() {
        listener()
    }
}