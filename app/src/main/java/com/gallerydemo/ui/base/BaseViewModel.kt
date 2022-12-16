package com.gallerydemo.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gallerydemo.ui.main.GalleryEvents
import com.gallerydemo.utils.observable.SingleLiveEvent

abstract class BaseViewModel : ViewModel() {

    protected val eventsLiveData: MutableLiveData<@GalleryEvents Int> = SingleLiveEvent()

    fun getEventsLiveData(): LiveData<@GalleryEvents Int> = eventsLiveData

}