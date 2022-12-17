package com.gallerydemo.ui.main.media

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.gallerydemo.BR
import com.gallerydemo.R
import com.gallerydemo.data.local.models.MediaItem
import com.gallerydemo.databinding.FragmentMediaListBinding
import com.gallerydemo.ui.base.BaseFragment
import com.gallerydemo.ui.main.GallerySharedViewModel
import com.gallerydemo.ui.main.ON_BACK_PRESS
import com.gallerydemo.ui.main.media.adapter.MediaListAdapter
import com.gallerydemo.utils.GalleryEqualGapItemDecoration
import com.gallerydemo.utils.callback.OnItemClickCallback
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MediaListFragment :
    BaseFragment<FragmentMediaListBinding, MediaListViewModel>(R.layout.fragment_media_list),
    OnItemClickCallback<MediaItem> {

    @Inject
    lateinit var mediaListAdapter: MediaListAdapter
    private val gallerySharedViewModel: GallerySharedViewModel by lazy {
        ViewModelProvider(requireActivity())[GallerySharedViewModel::class.java]
    }
    private val gridItemDecoration: GalleryEqualGapItemDecoration by lazy {
        GalleryEqualGapItemDecoration(
            resources.getDimension(R.dimen.grid_media_item_spacing).toInt()
        )
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

    private fun setGridSpanLookup() {
        val gridLayoutManager = if (bindings.rvMedia.layoutManager is GridLayoutManager) {
            bindings.rvMedia.layoutManager as GridLayoutManager
        } else {
            GridLayoutManager(
                context, resources.getInteger(R.integer.media_grid_span_count)
            ).also {
                bindings.rvMedia.layoutManager = it
            }
        }
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when {
                    mediaListAdapter.isListEmpty -> gridLayoutManager.spanCount
                    else -> 1
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        mediaListAdapter.onItemClickCallback = this@MediaListFragment
        setGridSpanLookup()
        bindings.rvMedia.apply {
            addItemDecoration(gridItemDecoration)
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

    override fun onItemClick(item: MediaItem) {
        showMediaInformationDialog(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.let { Glide.get(it).clearMemory() }
    }

    private fun createMediaDetailMessage(mediaItem: MediaItem): StringBuilder {
        return StringBuilder().append(getString(R.string.path))
            .append(": ").append(mediaItem.path).append("\n")
            .append(getString(R.string.mimeType))
            .append(": ").append(mediaItem.mimeType).append("\n")
            .append(getString(R.string.resolution))
            .append(": ").append(mediaItem.width).append("x").append(mediaItem.height).append("\n")
            .append(getString(R.string.size))
            .append(": ").append(mediaItem.size)
    }

    private fun showMediaInformationDialog(mediaItem: MediaItem) {
        context?.let {
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(it)
                .setTitle(getString(R.string.detail))
                .setMessage(createMediaDetailMessage(mediaItem))
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                }
            alertDialog.show()
        }
    }

}