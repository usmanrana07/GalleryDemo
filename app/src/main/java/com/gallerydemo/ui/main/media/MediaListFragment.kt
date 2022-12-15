package com.gallerydemo.ui.main.media

import android.os.Bundle
import android.view.View
import com.gallerydemo.BR
import com.gallerydemo.R
import com.gallerydemo.databinding.FragmentMediaListBinding
import com.gallerydemo.ui.base.BaseFragment
import com.gallerydemo.ui.main.media.adapter.MediaListAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MediaListFragment :
    BaseFragment<FragmentMediaListBinding, MediaListViewModel>(R.layout.fragment_media_list) {

    @Inject
    lateinit var mediaListAdapter: MediaListAdapter

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

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        bindings.rvMedia.adapter = mediaListAdapter
    }

    private fun subscribeLiveDataObserver() {
        /*viewModel.mediaLiveData.observe(this) {
            mediaListAdapter.setData(it)
        }*/
    }

}