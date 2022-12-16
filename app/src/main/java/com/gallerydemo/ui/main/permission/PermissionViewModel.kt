package com.gallerydemo.ui.main.permission

import androidx.lifecycle.ViewModel
import com.gallerydemo.utils.printLog
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor() : ViewModel() {

    fun onAllowClicked() {
        printLog("usm_gallery", "onAllowClicked")
    }

}