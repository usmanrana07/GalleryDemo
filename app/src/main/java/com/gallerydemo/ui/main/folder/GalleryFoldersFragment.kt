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
import com.gallerydemo.utils.GalleryEqualGapItemDecoration
import com.gallerydemo.utils.callback.OnItemClickCallback
import com.gallerydemo.utils.callback.StringResProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class GalleryFoldersFragment :
    BaseFragment<FragmentGalleryFoldersBinding, GalleryFoldersViewModel>(R.layout.fragment_gallery_folders),
    OnItemClickCallback<GalleryFolder> {

    @Inject
    lateinit var foldersAdapter: GalleryFoldersAdapter
    private var _gridLayoutManager: GridLayoutManager? = null
    private var _linearLayoutManager: LinearLayoutManager? = null
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

    override fun getViewModelClass(): Class<GalleryFoldersViewModel> {
        return GalleryFoldersViewModel::class.java
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
        subscribeLiveDataObserver()
        viewModel.fetchGalleryMedia(
            contentResolverProvider = { requireContext().contentResolver },
            stringProvider = object : StringResProvider {
                override fun getString(resId: Int): String {
                    return this@GalleryFoldersFragment.getString(resId)
                }

            })
    }

    private fun getGridLayoutManager(): GridLayoutManager {
        return _gridLayoutManager ?: kotlin.run {
            val layoutManager = if (bindings.rvFolders.layoutManager is GridLayoutManager) {
                bindings.rvFolders.layoutManager as GridLayoutManager
            } else {
                GridLayoutManager(
                    context, resources.getInteger(R.integer.folders_grid_span_count)
                )
            }
            _gridLayoutManager = layoutManager
            layoutManager
        }
    }

    private fun getLinearLayoutManager(): LinearLayoutManager {
        return _linearLayoutManager ?: kotlin.run {
            LinearLayoutManager(context).also {
                _linearLayoutManager = it
            }
        }
    }

    private fun setGridSpanLookup() {
        val gridLayoutManager = getGridLayoutManager()
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when {
                    foldersAdapter.isListEmpty -> gridLayoutManager.spanCount
                    else -> 1
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        foldersAdapter.onItemClickCallback = this@GalleryFoldersFragment
        updateFoldersRVLayoutManager(viewModel.folderModeObservable.getChecked())
        bindings.rvFolders.apply {
            if (foldersAdapter.isGridView) {
                setGridSpanLookup()
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
        if (event == TOGGLE_TO_LINEAR_VIEW)
            updateFoldersRVLayoutManager(true)
        else if (event == TOGGLE_TO_GRID_VIEW)
            updateFoldersRVLayoutManager(false)
    }

    private fun updateFoldersRVLayoutManager(showLinear: Boolean) {
        val layoutManager = if (showLinear) {
            bindings.rvFolders.removeItemDecoration(gridItemDecoration)
            getLinearLayoutManager()
        } else {
            bindings.rvFolders.addItemDecoration(gridItemDecoration)
            getGridLayoutManager()
        }
        if (bindings.rvFolders.layoutManager != layoutManager)
            bindings.rvFolders.layoutManager = layoutManager
        foldersAdapter.isGridView = !showLinear
    }

    override fun onItemClick(item: GalleryFolder) {
        gallerySharedViewModel.selectedFolder.value = item
        val navController = findNavController(bindings.root)
        navController.navigate(R.id.action_foldersFragment_to_mediaListFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // clear the references to avoid 'is already attached to a RecyclerView' exception on re-create view
        _linearLayoutManager = null
        _gridLayoutManager = null
    }
}