package com.gallerydemo.ui.main.folder

import android.content.ContentResolver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gallerydemo.data.local.models.GalleryFolder
import com.gallerydemo.ui.base.BaseViewModel
import com.gallerydemo.ui.main.TOGGLE_TO_GRID_VIEW
import com.gallerydemo.ui.main.TOGGLE_TO_LINEAR_VIEW
import com.gallerydemo.utils.State
import com.gallerydemo.utils.callback.StringResProvider
import com.gallerydemo.utils.observable.FoldersModeObservable
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
        eventsLiveData.postValue(if (showLinear) TOGGLE_TO_LINEAR_VIEW else TOGGLE_TO_GRID_VIEW)
    }

    val isLoading = MutableLiveData(false)

    fun getFoldersLiveData(): LiveData<List<GalleryFolder>> = _foldersLiveData

    /**
     * This function first checks that if data is already loaded then on recreate of view if we call
     * this method again then it returns until user wants to forceRefresh.
     * We've used lambdas to get string or contentResolver because we don't have useless initialization
     * before passing the return guard.
     */
    fun fetchGalleryMedia(
        contentResolverProvider: () -> ContentResolver,
        stringProvider: StringResProvider,
        forceRefresh: Boolean = false
    ) {
        _foldersLiveData.value?.takeUnless { it.isEmpty() }?.let {
            if (!forceRefresh)
                return // we already have loaded data
        }
        viewModelScope.launch {
            repository.loadMediaFromStorage(
                contentResolver = contentResolverProvider(),
                stringProvider = stringProvider
            ).collectLatest { apiState ->
                when (apiState) {
                    is State.Loading -> isLoading.postValue(true)
                    is State.Success -> {
                        isLoading.postValue(false)
                        apiState.data.let {
                            _foldersLiveData.postValue(it)
                        }
                    }
                    is State.Failure -> isLoading.postValue(false)
                }
            }
        }
    }
}