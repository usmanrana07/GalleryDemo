package com.gallerydemo.ui.main.folder

import android.content.ContentResolver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gallerydemo.data.local.models.GalleryFolder
import com.gallerydemo.ui.base.BaseViewModel
import com.gallerydemo.ui.main.TOGGLE_TO_GRID_VIEW
import com.gallerydemo.ui.main.TOGGLE_TO_LINEAR_VIEW
import com.gallerydemo.utils.FoldersModeObservable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryFoldersViewModel @Inject constructor(private val repository: GalleryRepository) :
    BaseViewModel() {

    private val _foldersLiveData: MutableLiveData<List<GalleryFolder>> = MutableLiveData()
    var folderModeObservable: FoldersModeObservable = FoldersModeObservable(
        false
    ) { showLinear: Boolean ->
        eventsLiveData.postValue(if(showLinear) TOGGLE_TO_LINEAR_VIEW else TOGGLE_TO_GRID_VIEW)
    }

    fun getFoldersLiveData(): LiveData<List<GalleryFolder>> = _foldersLiveData

    fun fetchGalleryMedia(contentResolver: ContentResolver) {
        viewModelScope.launch {
            repository.loadMediaFromStorage(contentResolver).collectLatest {
                _foldersLiveData.postValue(it)
            }
        }
    }

}