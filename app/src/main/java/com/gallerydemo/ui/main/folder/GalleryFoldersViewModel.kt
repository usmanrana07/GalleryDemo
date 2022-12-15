package com.gallerydemo.ui.main.folder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gallerydemo.utils.FoldersModeObservable
import com.gallerydemo.utils.printLog

class GalleryFoldersViewModel : ViewModel() {

    var enableColumnSwitching: MutableLiveData<Boolean> = MutableLiveData()
    var folderModeObservable: FoldersModeObservable = FoldersModeObservable(
        false
    ) { showLinear: Boolean ->
        printLog("usm_gallery", "showLinear= $showLinear")
    }

}