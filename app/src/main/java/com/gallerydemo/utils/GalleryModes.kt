package com.gallerydemo.utils

import androidx.annotation.IntDef

@IntDef(GALLERY_IMAGE, GALLERY_VIDEO, GALLERY_IMAGE_AND_VIDEOS)
annotation class GalleryModes

const val GALLERY_IMAGE = 0
const val GALLERY_VIDEO = 1
const val GALLERY_IMAGE_AND_VIDEOS = 2