package com.gallerydemo.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gallerydemo.ui.main.GalleryEvents
import com.gallerydemo.utils.printLog

abstract class BaseFragment<DB : ViewDataBinding, VM : BaseViewModel>(@LayoutRes private val layoutResId: Int) :
    Fragment() {

    private var _bindings: DB? = null

    // This property is only valid between onCreateView and onDestroyView.
    protected val bindings get() = _bindings!!
    protected lateinit var viewModel: VM

    protected abstract fun getBindingVariable(): Int
    protected abstract fun getViewModelClass(): Class<VM>

    protected abstract fun onEventReceived(@GalleryEvents event: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[getViewModelClass()]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bindings = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        bindings.lifecycleOwner = viewLifecycleOwner
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindings.setVariable(getBindingVariable(), viewModel)
        setEventsObserver()
    }

    private fun setEventsObserver() {
        viewModel.getEventsLiveData().observe(viewLifecycleOwner) {
            printLog("usm_test_folder", "getEventsLiveData")
            onEventReceived(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bindings = null
    }

}