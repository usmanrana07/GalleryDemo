package com.gallerydemo.ui.main.folder

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.gallerydemo.BR
import com.gallerydemo.R
import com.gallerydemo.data.local.models.GalleryFolder
import com.gallerydemo.databinding.FragmentGalleryFoldersBinding
import com.gallerydemo.ui.base.BaseFragment
import com.gallerydemo.ui.main.GallerySharedViewModel
import com.gallerydemo.ui.main.TOGGLE_TO_GRID_VIEW
import com.gallerydemo.ui.main.TOGGLE_TO_LINEAR_VIEW
import com.gallerydemo.ui.main.folder.adapter.GalleryFoldersAdapter
import com.gallerydemo.ui.main.folder.adapter.GalleryFoldersAdapterInterface
import com.gallerydemo.utils.GalleryEqualGapItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class GalleryFoldersFragment :
    BaseFragment<FragmentGalleryFoldersBinding, GalleryFoldersViewModel>(R.layout.fragment_gallery_folders),
    GalleryFoldersAdapterInterface {

    @Inject
    lateinit var foldersAdapter: GalleryFoldersAdapter
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
        if (bindings.rvFolders.layoutManager is GridLayoutManager) {
            bindings.rvFolders.layoutManager as GridLayoutManager
        } else {
            GridLayoutManager(
                context,
                resources.getInteger(R.integer.folders_grid_span_count)
            ).apply {
                bindings.rvFolders.layoutManager = this
            }
        }
    }
    private val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(context)
    }

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

        setUpRecyclerView()
        subscribeLiveDataObserver()
        viewModel.fetchGalleryMedia(requireContext().contentResolver)
    }

    private fun setUpRecyclerView() {
        foldersAdapter.adapterInterface = this@GalleryFoldersFragment
        bindings.rvFolders.apply {
            addItemDecoration(gridItemDecoration)
            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when {
                        foldersAdapter.isListEmpty -> gridLayoutManager.spanCount
                        else -> 1
                    }
                }
            }
            adapter = foldersAdapter
        }
    }

    private fun subscribeLiveDataObserver() {
        viewModel.getFoldersLiveData().observe(viewLifecycleOwner) {
            foldersAdapter.setData(it)
        }
    }

    override fun onEventReceived(event: Int) {
        when (event) {
            TOGGLE_TO_LINEAR_VIEW -> onToggleFolderViewMode(true)
            TOGGLE_TO_GRID_VIEW -> onToggleFolderViewMode(false)
            else -> {}
        }
    }

    private fun onToggleFolderViewMode(showLinear: Boolean) {
        bindings.rvFolders.layoutManager = if (showLinear) {
            bindings.rvFolders.removeItemDecoration(gridItemDecoration)
            linearLayoutManager
        } else {
            bindings.rvFolders.addItemDecoration(gridItemDecoration)
            gridLayoutManager
        }
        foldersAdapter.isGridView = !showLinear
    }

    override fun onItemClick(folder: GalleryFolder) {
        gallerySharedViewModel.selectedFolder.value = folder
        val navController = findNavController(bindings.root)
        navController.navigate(R.id.action_foldersFragment_to_mediaListFragment)
    }

}