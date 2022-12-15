package com.gallerydemo.ui.main.folder.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.gallerydemo.BR
import com.gallerydemo.databinding.ItemFoldersEmptyViewBinding
import com.gallerydemo.databinding.ItemGridFolderViewBinding
import com.gallerydemo.databinding.ItemLinearFolderViewBinding
import com.gallerydemo.ui.base.BaseRecyclerViewAdapter
import com.gallerydemo.ui.base.BaseViewHolder
import com.gallerydemo.ui.main.folder.FolderItemViewModel
import com.gallerydemo.ui.main.folder.GalleryFolder
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
        return if (isGridView) createGridItemViewHolder(parent) else createLinearItemViewHolder(
            parent
        )
    }

    private fun createGridItemViewHolder(parent: ViewGroup): BaseViewHolder {
        val itemBinding: ItemGridFolderViewBinding = ItemGridFolderViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        itemBinding.lifecycleOwner = parent.findViewTreeLifecycleOwner()

        return FolderGridItemViewHolder(itemBinding, this)
    }

    private fun createLinearItemViewHolder(parent: ViewGroup): BaseViewHolder {
        val itemBinding: ItemLinearFolderViewBinding = ItemLinearFolderViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        itemBinding.lifecycleOwner = parent.findViewTreeLifecycleOwner()
        return FolderLinearItemViewHolder(itemBinding, this)
    }

    override fun createEmptyItemViewHolder(parent: ViewGroup): BaseViewHolder {
        val itemBinding: ItemFoldersEmptyViewBinding = ItemFoldersEmptyViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
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