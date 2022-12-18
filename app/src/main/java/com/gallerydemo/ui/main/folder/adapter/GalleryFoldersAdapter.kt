package com.gallerydemo.ui.main.folder.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.gallerydemo.BR
import com.gallerydemo.R
import com.gallerydemo.data.local.models.GalleryFolder
import com.gallerydemo.databinding.ItemEmptyListViewBinding
import com.gallerydemo.databinding.ItemGridFolderViewBinding
import com.gallerydemo.databinding.ItemLinearFolderViewBinding
import com.gallerydemo.ui.base.BaseRecyclerViewAdapter
import com.gallerydemo.ui.base.BaseViewHolder
import com.gallerydemo.utils.callback.ItemViewHolderCallback
import com.gallerydemo.utils.callback.OnItemClickCallback
import javax.inject.Inject

class GalleryFoldersAdapter @Inject constructor() : BaseRecyclerViewAdapter<BaseViewHolder>(),
    GalleryFoldersViewHolderInterface {
    private val viewTypeLinear = 2
    private val dataList: MutableList<GalleryFolder> = mutableListOf()
    val isListEmpty: Boolean get() = dataList.isEmpty()
    var onItemClickCallback: OnItemClickCallback<GalleryFolder>? = null
    var isGridView = true
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            if (field != value) {
                field = value
                if (dataList.isNotEmpty())
                    notifyDataSetChanged()
            }
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

    override fun onItemClick(item: GalleryFolder) {
        onItemClickCallback?.onItemClick(item)
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            dataList.isEmpty() -> viewTypeEmpty
            isGridView -> viewTypeNormal
            else -> viewTypeLinear
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            viewTypeLinear -> createLinearItemViewHolder(parent)
            else -> super.onCreateViewHolder(parent, viewType)
        }
    }

    override fun createNormalItemViewHolder(parent: ViewGroup): BaseViewHolder {
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
        val itemBinding: ItemEmptyListViewBinding = bindRecyclerViewItem(
            parent,
            R.layout.item_empty_list_view
        )
        return EmptyItemViewHolder(
            itemBinding,
            parent.context.getString(R.string.empty_folders_list)
        )
    }

    override fun getItemCount(): Int {
        return if (dataList.isNotEmpty()) dataList.size else 1
    }

    private abstract class BaseItemViewHolder<VB : ViewDataBinding>(
        val binding: VB,
        val listener: GalleryFoldersViewHolderInterface
    ) : BaseViewHolder(binding.root) {

        protected abstract val ivThumbnail: ImageView

        override fun onBind(position: Int) {
            val folder = listener.getItem(position)
            val itemViewModel = FolderItemViewModel(folder) {
                listener.onItemClick(folder)
            }
            binding.setVariable(BR.viewModel, itemViewModel)
            itemViewModel.thumbnail?.let { loadThumbnail(it) } ?: kotlin.run {
                ivThumbnail.setImageResource(R.drawable.ic_default_thumbnail)
            }
        }

        private fun loadThumbnail(thumbnail: String) {
            Glide.with(ivThumbnail).load(thumbnail)
                .placeholder(R.drawable.ic_default_thumbnail)//.sizeMultiplier(0.1f)
                .into(ivThumbnail)
        }
    }

    private class FolderGridItemViewHolder(
        binding: ItemGridFolderViewBinding,
        listener: GalleryFoldersViewHolderInterface,
        override val ivThumbnail: ImageView = binding.ivThumbnail
    ) : BaseItemViewHolder<ItemGridFolderViewBinding>(binding, listener)

    private class FolderLinearItemViewHolder(
        binding: ItemLinearFolderViewBinding,
        listener: GalleryFoldersViewHolderInterface,
        override val ivThumbnail: ImageView = binding.ivThumbnail
    ) : BaseItemViewHolder<ItemLinearFolderViewBinding>(binding, listener)


}

interface GalleryFoldersViewHolderInterface : ItemViewHolderCallback<GalleryFolder>