package com.gallerydemo.ui.main.permission

import androidx.lifecycle.ViewModel
import com.gallerydemo.utils.printLog

class PermissionViewModel : ViewModel() {

    fun onAllowClicked() {
        printLog("usm_gallery", "onAllowClicked")
    }

}