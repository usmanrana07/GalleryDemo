package com.gallerydemo.ui.main.folder

import android.os.Bundle
import android.view.View
import com.gallerydemo.BR
import com.gallerydemo.R
import com.gallerydemo.databinding.FragmentGalleryFoldersBinding
import com.gallerydemo.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFoldersFragment :
    BaseFragment<FragmentGalleryFoldersBinding, GalleryFoldersViewModel>(R.layout.fragment_gallery_folders) {

    companion object {
        fun newInstance() = GalleryFoldersFragment()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getViewModelClass(): Class<GalleryFoldersViewModel> {
        return GalleryFoldersViewModel::class.java
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.enableColumnSwitching.postValue(true)

    }

}