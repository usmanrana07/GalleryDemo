package com.gallerydemo.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gallerydemo.data.local.models.GalleryFolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GallerySharedViewModel @Inject constructor() : ViewModel() {

    var selectedFolder: MutableLiveData<GalleryFolder> = MutableLiveData()
}