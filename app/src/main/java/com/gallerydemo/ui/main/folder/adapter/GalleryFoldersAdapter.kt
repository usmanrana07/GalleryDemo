package com.gallerydemo.ui.main.folder.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.gallerydemo.BR
import com.gallerydemo.R
import com.gallerydemo.databinding.ItemFoldersEmptyViewBinding
import com.gallerydemo.databinding.ItemGridFolderViewBinding
import com.gallerydemo.databinding.ItemLinearFolderViewBinding
import com.gallerydemo.ui.base.BaseRecyclerViewAdapter
import com.gallerydemo.ui.base.BaseViewHolder
import com.gallerydemo.data.local.models.GalleryFolder
import com.gallerydemo.utils.printLog
import javax.inject.Inject

class GalleryFoldersAdapter @Inject constructor() : BaseRecyclerViewAdapter<BaseViewHolder>(),
    GalleryFoldersViewHolderInterface {

    private val dataList: MutableList<GalleryFolder> = mutableListOf()
    var isGridView = true
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            if (dataList.isNotEmpty())
                notifyDataSetChanged()
            field = value
        }


    @SuppressLint("NotifyDataSetChanged")
    fun setData(folders: List<GalleryFolder>) {
        if (dataList.isNotEmpty())
            dataList.clear()
        dataList.addAll(folders)

        notifyDataSetChanged()
    }

    override fun getItem(position: Int): GalleryFolder {
        return dataList[position]
    }

    override fun onItemClick() {
        printLog("usm_gallery", "folder is clicked")
    }

    override fun createNormalItemViewHolder(parent: ViewGroup): BaseViewHolder {
        return if (isGridView)
            createGridItemViewHolder(parent)
        else
            createLinearItemViewHolder(parent)
    }

    private fun createGridItemViewHolder(parent: ViewGroup): BaseViewHolder {
        return FolderGridItemViewHolder(
            bindRecyclerViewItem(
                parent,
                R.layout.item_grid_folder_view
            ), listener = this
        )
    }

    private fun createLinearItemViewHolder(parent: ViewGroup): BaseViewHolder {
        return FolderLinearItemViewHolder(
            bindRecyclerViewItem(
                parent,
                R.layout.item_linear_folder_view
            ), listener = this
        )
    }

    override fun createEmptyItemViewHolder(parent: ViewGroup): BaseViewHolder {
        val itemBinding: ItemFoldersEmptyViewBinding = bindRecyclerViewItem(
            parent,
            R.layout.item_folders_empty_view
        )
        return EmptyItemViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return if (dataList.isNotEmpty()) dataList.size else 1
    }

    private abstract class BaseItemViewHolder<VB : ViewDataBinding>(
        val binding: VB,
        val listener: GalleryFoldersViewHolderInterface
    ) : BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            val folder = listener.getItem(position)
            val itemViewModel = FolderItemViewModel(folder) {
                onFolderClicked()
            }
            binding.setVariable(BR.viewModel, itemViewModel)
        }

        protected fun onFolderClicked() {
            listener.onItemClick()
        }
    }

    private class FolderGridItemViewHolder(
        binding: ItemGridFolderViewBinding,
        listener: GalleryFoldersViewHolderInterface
    ) : BaseItemViewHolder<ItemGridFolderViewBinding>(binding, listener)

    private class FolderLinearItemViewHolder(
        binding: ItemLinearFolderViewBinding,
        listener: GalleryFoldersViewHolderInterface
    ) : BaseItemViewHolder<ItemLinearFolderViewBinding>(binding, listener)


}

interface GalleryFoldersViewHolderInterface {
    fun onItemClick()
    fun getItem(position: Int): GalleryFolder
}