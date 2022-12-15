package com.gallerydemo.ui.main.media

import android.os.Bundle
import android.view.View
import com.gallerydemo.BR
import com.gallerydemo.R
import com.gallerydemo.databinding.FragmentMediaListBinding
import com.gallerydemo.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MediaListFragment :
    BaseFragment<FragmentMediaListBinding, MediaListViewModel>(R.layout.fragment_media_list) {

    companion object {
        fun newInstance() = MediaListFragment()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getViewModelClass(): Class<MediaListViewModel> {
        return MediaListViewModel::class.java
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}