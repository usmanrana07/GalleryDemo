package com.gallerydemo.ui.main.permission

import com.gallerydemo.ui.base.BaseViewModel
import com.gallerydemo.ui.main.ON_ALLOW
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor() : BaseViewModel() {

    fun onAllowClicked() {
        eventsLiveData.postValue(ON_ALLOW)
    }

}