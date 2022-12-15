package com.gallerydemo.ui.main.folder

class FolderItemViewModel(folder: GalleryFolder, private val listener: () -> Unit) {
    val thumbnail: String? = folder.thumbnail
    val title: String = folder.title
    val count: Int = folder.count

    fun onItemClick() {
        listener()
    }
}