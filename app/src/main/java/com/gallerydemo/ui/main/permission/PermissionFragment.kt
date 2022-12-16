package com.gallerydemo.ui.main.permission

import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
import com.gallerydemo.BR
import com.gallerydemo.R
import com.gallerydemo.databinding.FragmentPermissionBinding
import com.gallerydemo.ui.base.BaseFragment
import com.gallerydemo.utils.PermissionsHelper

class PermissionFragment :
    BaseFragment<FragmentPermissionBinding, PermissionViewModel>(R.layout.fragment_permission) {

    private val permissionsHelper: PermissionsHelper by lazy { PermissionsHelper(this) }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getViewModelClass(): Class<PermissionViewModel> {
        return PermissionViewModel::class.java
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!permissionsHelper.hasReadStoragePermission()) {
            permissionsHelper.setLifecycleOwner(this)
            permissionsHelper.requestStorageReadPermission()
        } else {
            val navController = Navigation.findNavController(bindings.root)
            navController.navigate(R.id.action_permissionFragment_to_foldersFragment)
        }
    }

}