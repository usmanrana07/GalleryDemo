package com.gallerydemo.utils.callback

interface ItemViewHolderCallback<T> : OnItemClickCallback<T> {
    fun getItem(position: Int): T
}