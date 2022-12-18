package com.gallerydemo.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.gallerydemo.BR
import com.gallerydemo.ui.main.folder.adapter.EmptyItemViewModel

abstract class BaseRecyclerViewAdapter<T : BaseViewHolder> : RecyclerView.Adapter<T>() {

    protected val viewTypeNormal = 0
    protected val viewTypeEmpty = 1

    protected abstract fun createNormalItemViewHolder(parent: ViewGroup): T
    protected abstract fun createEmptyItemViewHolder(parent: ViewGroup): T

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): T {
        return when (viewType) {
            viewTypeEmpty -> createEmptyItemViewHolder(parent)
            else -> createNormalItemViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: T, position: Int) {
        holder.onBind(position)
    }

    protected fun <T : ViewDataBinding> bindRecyclerViewItem(parent: ViewGroup, layoutId: Int): T {
        return DataBindingUtil.inflate<T>(
            LayoutInflater.from(parent.context),
            layoutId,
            parent,
            false
        ).apply {
            lifecycleOwner = parent.findViewTreeLifecycleOwner()
        }
    }

    protected class EmptyItemViewHolder<VB : ViewDataBinding>(
        private val binding: VB,
        private val message: String
    ) : BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            binding.setVariable(BR.viewModel, EmptyItemViewModel(message))
        }
    }

}