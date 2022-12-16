package com.gallerydemo.utils.observable

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.gallerydemo.BR

class FoldersModeObservable(
    private var checked: Boolean,
    private val onCheckChangeListener: (showLinear: Boolean) -> Unit
) : BaseObservable() {
    @Bindable
    fun getChecked(): Boolean {
        return checked
    }

    fun setChecked(checked: Boolean) {
        this.checked = checked
        notifyPropertyChanged(BR.checked)
        onCheckChangeListener(checked)
    }

}