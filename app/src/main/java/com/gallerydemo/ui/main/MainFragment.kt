package com.gallerydemo.ui.main

import android.os.Bundle
import android.view.View
import com.gallerydemo.BR
import com.gallerydemo.R
import com.gallerydemo.databinding.FragmentMainBinding
import com.gallerydemo.ui.base.BaseFragment

class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>(R.layout.fragment_main) {

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getViewModelClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.enableColumnSwitching.postValue(true)
    }

}