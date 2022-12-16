package com.gallerydemo.ui.main.media

import androidx.lifecycle.MutableLiveData
import com.gallerydemo.ui.base.BaseViewModel
import com.gallerydemo.ui.main.ON_BACK_PRESS
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MediaListViewModel @Inject constructor() : BaseViewModel() {

    val folderTitle: MutableLiveData<String> = MutableLiveData()

    fun onBackClick() {
        eventsLiveData.postValue(ON_BACK_PRESS)
    }

}