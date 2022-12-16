package com.gallerydemo.ui.main.media

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.gallerydemo.BR
import com.gallerydemo.R
import com.gallerydemo.databinding.FragmentMediaListBinding
import com.gallerydemo.ui.base.BaseFragment
import com.gallerydemo.ui.main.GallerySharedViewModel
import com.gallerydemo.ui.main.ON_BACK_PRESS
import com.gallerydemo.ui.main.media.adapter.MediaListAdapter
import com.gallerydemo.utils.GalleryEqualGapItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MediaListFragment :
    BaseFragment<FragmentMediaListBinding, MediaListViewModel>(R.layout.fragment_media_list) {

    @Inject
    lateinit var mediaListAdapter: MediaListAdapter
    private val gallerySharedViewModel: GallerySharedViewModel by lazy {
        ViewModelProvider(requireActivity())[GallerySharedViewModel::class.java]
    }
    private val gridItemDecoration: GalleryEqualGapItemDecoration by lazy {
        GalleryEqualGapItemDecoration(
            gridLayoutManager.spanCount,
            resources.getDimension(R.dimen.grid_media_item_spacing).toInt()
        )
    }
    private val gridLayoutManager: GridLayoutManager by lazy {
        if (bindings.rvMedia.layoutManager is GridLayoutManager) {
            bindings.rvMedia.layoutManager as GridLayoutManager
        } else {
            GridLayoutManager(
                context,
                resources.getInteger(R.integer.media_grid_span_count)
            ).apply {
                bindings.rvMedia.layoutManager = this
            }
        }
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
        subscribeLiveDataObserver()

    }

    private fun setUpRecyclerView() {

        bindings.rvMedia.apply {
            addItemDecoration(gridItemDecoration)
            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when {
                        mediaListAdapter.isListEmpty -> gridLayoutManager.spanCount
                        else -> 1
                    }
                }
            }
            adapter = mediaListAdapter
        }
    }

    private fun subscribeLiveDataObserver() {
        gallerySharedViewModel.selectedFolder.observe(viewLifecycleOwner) {
            viewModel.folderTitle.value = it.title
            mediaListAdapter.setData(it.mediaList)
        }
    }

    override fun onEventReceived(event: Int) {
        if (event == ON_BACK_PRESS) {
            val navController = Navigation.findNavController(bindings.root)
            navController.popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Glide.get(requireContext()).clearMemory()
    }

}