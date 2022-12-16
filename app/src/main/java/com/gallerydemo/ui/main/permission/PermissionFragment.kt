package com.gallerydemo.ui.main.permission

import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
import com.gallerydemo.BR
import com.gallerydemo.R
import com.gallerydemo.databinding.FragmentPermissionBinding
import com.gallerydemo.ui.base.BaseFragment
import com.gallerydemo.ui.main.ON_ALLOW

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
        }
    }

    /**
     * navigating to folders screen in onResume because if user has goes to settings screen
     * and enables the permission then on resume we don't want to keep showing permissions screen.
     * We will navigate to next screen.
     */
    override fun onResume() {
        super.onResume()
        if (permissionsHelper.hasReadStoragePermission()) {
            val navController = Navigation.findNavController(bindings.root)
            navController.navigate(R.id.action_permissionFragment_to_foldersFragment)
        }
    }

    override fun onEventReceived(event: Int) {
        if (event == ON_ALLOW) {
            permissionsHelper.requestStorageReadPermission()
        }
    }

}