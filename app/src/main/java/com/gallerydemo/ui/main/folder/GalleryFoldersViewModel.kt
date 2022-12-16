package com.gallerydemo.ui.main.folder

import android.content.ContentResolver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gallerydemo.data.local.models.GalleryFolder
import com.gallerydemo.utils.FoldersModeObservable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryFoldersViewModel @Inject constructor(private val repository: GalleryRepository) :
    ViewModel() {

    private val _foldersLiveData: MutableLiveData<List<GalleryFolder>> = MutableLiveData()
    private val _toggleViewLiveData: MutableLiveData<Boolean> = MutableLiveData()
    var folderModeObservable: FoldersModeObservable = FoldersModeObservable(
        false
    ) { showLinear: Boolean ->
        _toggleViewLiveData.postValue(showLinear)
    }

    fun getFoldersLiveData(): LiveData<List<GalleryFolder>> = _foldersLiveData
    fun getToggleViewLiveData(): LiveData<Boolean> = _toggleViewLiveData

    fun fetchGalleryMedia(contentResolver: ContentResolver) {
        viewModelScope.launch {
            repository.loadMediaFromStorage(contentResolver).collectLatest {
                _foldersLiveData.postValue(it)
            }
        }
    }

}