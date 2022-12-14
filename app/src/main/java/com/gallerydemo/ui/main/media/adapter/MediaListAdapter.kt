package com.gallerydemo.ui.main.media.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.gallerydemo.BR
import com.gallerydemo.R
import com.gallerydemo.data.local.models.MediaItem
import com.gallerydemo.databinding.ItemEmptyListViewBinding
import com.gallerydemo.databinding.ItemMediaViewBinding
import com.gallerydemo.databinding.ItemVideoViewBinding
import com.gallerydemo.ui.base.BaseRecyclerViewAdapter
import com.gallerydemo.ui.base.BaseViewHolder
import com.gallerydemo.utils.callback.ItemViewHolderCallback
import com.gallerydemo.utils.callback.OnItemClickCallback
import javax.inject.Inject

class MediaListAdapter @Inject constructor() : BaseRecyclerViewAdapter<BaseViewHolder>(),
    GalleryMediaItemViewHolderInterface {
    private val viewTypeVideo = 2
    private val dataList: MutableList<MediaItem> = mutableListOf()
    val isListEmpty: Boolean get() = dataList.isEmpty()
    var onItemClickCallback: OnItemClickCallback<MediaItem>? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(folders: List<MediaItem>) {
        if (dataList.isNotEmpty())
            dataList.clear()
        dataList.addAll(folders)

        notifyDataSetChanged()
    }

    override fun getItem(position: Int): MediaItem {
        return dataList[position]
    }

    override fun onItemClick(item: MediaItem) {
        onItemClickCallback?.onItemClick(item)
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            dataList.isEmpty() -> viewTypeEmpty
            getItem(position).isVideo -> viewTypeVideo
            else -> viewTypeNormal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            viewTypeVideo -> createVideoItemViewHolder(parent)
            else -> super.onCreateViewHolder(parent, viewType)
        }
    }

    override fun createNormalItemViewHolder(parent: ViewGroup): BaseViewHolder {
        return ImageMediaItemViewHolder(
            bindRecyclerViewItem(
                parent,
                R.layout.item_media_view
            ), listener = this
        )
    }

    private fun createVideoItemViewHolder(parent: ViewGroup): BaseViewHolder {
        return VideoMediaItemViewHolder(
            bindRecyclerViewItem(
                parent,
                R.layout.item_video_view
            ), listener = this
        )
    }

    override fun createEmptyItemViewHolder(parent: ViewGroup): BaseViewHolder {
        val itemBinding: ItemEmptyListViewBinding = bindRecyclerViewItem(
            parent,
            R.layout.item_empty_list_view
        )
        return EmptyItemViewHolder(itemBinding, parent.context.getString(R.string.empty_media_list))
    }

    override fun getItemCount(): Int {
        return if (dataList.isNotEmpty()) dataList.size else 1
    }

    private abstract class BaseItemViewHolder<VB : ViewDataBinding>(
        val binding: VB,
        val listener: GalleryMediaItemViewHolderInterface
    ) : BaseViewHolder(binding.root) {
        protected abstract val ivThumbnail: ImageView
        override fun onBind(position: Int) {
            val item = listener.getItem(position)
            val itemViewModel = MediaListItemViewModel(item) {
                listener.onItemClick(item)
            }
            binding.setVariable(BR.viewModel, itemViewModel)

            loadThumbnail(itemViewModel.thumbnail)
        }

        private fun loadThumbnail(thumbnail: String) {
            Glide.with(ivThumbnail).load(thumbnail)
                .placeholder(R.drawable.ic_default_thumbnail)
                .sizeMultiplier(0.5f)
                .into(ivThumbnail)
        }
    }

    private class ImageMediaItemViewHolder(
        binding: ItemMediaViewBinding,
        listener: GalleryMediaItemViewHolderInterface,
        override val ivThumbnail: ImageView = binding.ivThumbnail
    ) : BaseItemViewHolder<ItemMediaViewBinding>(binding, listener)

    private class VideoMediaItemViewHolder(
        binding: ItemVideoViewBinding,
        listener: GalleryMediaItemViewHolderInterface,
        override val ivThumbnail: ImageView = binding.ivThumbnail
    ) : BaseItemViewHolder<ItemVideoViewBinding>(binding, listener)

}

interface GalleryMediaItemViewHolderInterface : ItemViewHolderCallback<MediaItem>