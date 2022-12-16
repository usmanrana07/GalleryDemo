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
import com.gallerydemo.utils.printLog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class GalleryFoldersFragment :
    BaseFragment<FragmentGalleryFoldersBinding, GalleryFoldersViewModel>(R.layout.fragment_gallery_folders),
    GalleryFoldersAdapterInterface {

    @Inject
    lateinit var foldersAdapter: GalleryFoldersAdapter
    private var _gridLayoutManager: GridLayoutManager? = null
    private var _linearLayoutManager: LinearLayoutManager? = null
    private val gallerySharedViewModel: GallerySharedViewModel by lazy {
        ViewModelProvider(requireActivity())[GallerySharedViewModel::class.java]
    }
    private val gridItemDecoration: GalleryEqualGapItemDecoration by lazy {
        GalleryEqualGapItemDecoration(
            resources.getInteger(R.integer.media_grid_span_count),
            resources.getDimension(R.dimen.grid_media_item_spacing).toInt()
        )
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getViewModelClass(): Class<GalleryFoldersViewModel> {
        return GalleryFoldersViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        printLog("usm_test_folder", "onCreate")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
        subscribeLiveDataObserver()
        viewModel.fetchGalleryMedia(requireContext().contentResolver)
    }

    private fun getGridLayoutManager(): GridLayoutManager {
        return _gridLayoutManager ?: kotlin.run {
            val layoutManager = if (bindings.rvFolders.layoutManager is GridLayoutManager) {
                bindings.rvFolders.layoutManager as GridLayoutManager
            } else {
                GridLayoutManager(
                    context,
                    resources.getInteger(R.integer.folders_grid_span_count)
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

    private fun setUpRecyclerView() {
        foldersAdapter.adapterInterface = this@GalleryFoldersFragment
        updateFoldersRVLayoutManager(viewModel.folderModeObservable.getChecked())
        bindings.rvFolders.apply {
            if (foldersAdapter.isGridView) {
                val gridLayoutManager = getGridLayoutManager()
                gridLayoutManager.spanSizeLookup =
                    object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return when {
                                foldersAdapter.isListEmpty -> gridLayoutManager.spanCount
                                else -> 1
                            }
                        }
                    }
            }
            adapter = foldersAdapter
        }
    }

    private fun subscribeLiveDataObserver() {
        viewModel.getFoldersLiveData().observe(viewLifecycleOwner) {
            printLog("usm_test_folder", "subscribeLiveDataObserver: size= ${it.size}")
            foldersAdapter.setData(it)
        }
    }

    override fun onEventReceived(event: Int) {
        when (event) {
            TOGGLE_TO_LINEAR_VIEW -> updateFoldersRVLayoutManager(true)
            TOGGLE_TO_GRID_VIEW -> updateFoldersRVLayoutManager(false)
            else -> {}
        }
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

    override fun onItemClick(folder: GalleryFolder) {
        gallerySharedViewModel.selectedFolder.value = folder
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