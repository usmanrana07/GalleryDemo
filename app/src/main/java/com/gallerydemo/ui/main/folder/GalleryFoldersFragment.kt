package com.gallerydemo.ui.main.folder

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.gallerydemo.BR
import com.gallerydemo.R
import com.gallerydemo.databinding.FragmentGalleryFoldersBinding
import com.gallerydemo.ui.base.BaseFragment
import com.gallerydemo.ui.main.folder.adapter.GalleryFoldersAdapter
import com.gallerydemo.utils.GalleryEqualGapItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GalleryFoldersFragment :
    BaseFragment<FragmentGalleryFoldersBinding, GalleryFoldersViewModel>(R.layout.fragment_gallery_folders) {

    @Inject
    lateinit var foldersAdapter: GalleryFoldersAdapter
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

    }

    private fun setUpRecyclerView() {
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
        /*viewModel.foldersLiveData.observe(this) {
            foldersAdapter.setData(it)
        }*/
    }

    fun onToggleFolderViewMode(showLinear: Boolean) {
        bindings.rvFolders.layoutManager = if (showLinear) {
            bindings.rvFolders.removeItemDecoration(gridItemDecoration)
            linearLayoutManager
        } else {
            bindings.rvFolders.addItemDecoration(gridItemDecoration)
            gridLayoutManager
        }
        foldersAdapter.isGridView = !showLinear
    }

}