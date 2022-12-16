package com.gallerydemo.ui.main

import androidx.annotation.IntDef

@Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
@IntDef(ON_ALLOW, ON_BACK_PRESS, TOGGLE_TO_LINEAR_VIEW, TOGGLE_TO_GRID_VIEW)
annotation class GalleryEvents

const val ON_ALLOW = 0
const val ON_BACK_PRESS = 1
const val TOGGLE_TO_LINEAR_VIEW = 2
const val TOGGLE_TO_GRID_VIEW = 3
