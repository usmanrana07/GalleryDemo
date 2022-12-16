package com.gallerydemo.ui.main.media

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MediaListViewModel @Inject constructor() : ViewModel() {

    val folderTitle: MutableLiveData<String> = MutableLiveData()

}