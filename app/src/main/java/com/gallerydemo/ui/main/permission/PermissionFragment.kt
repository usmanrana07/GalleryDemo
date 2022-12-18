package com.gallerydemo.ui.main.permission

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import com.gallerydemo.BR
import com.gallerydemo.R
import com.gallerydemo.databinding.FragmentPermissionBinding
import com.gallerydemo.ui.base.BaseFragment
import com.gallerydemo.ui.main.ON_ALLOW
import kotlinx.coroutines.launch

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
            permissionsHelper.setLifecycleOwner(viewLifecycleOwner)
            checkPermissionOnEveryResume()
        } else {
            navigateToFoldersScreen()
        }
    }

    private fun navigateToFoldersScreen() {
        val navController = Navigation.findNavController(bindings.root)
        navController.navigate(R.id.action_permissionFragment_to_foldersFragment)
    }

    /**
     * If user goes to settings screen and enables the permission then on resume,
     * we don't want to keep showing permissions screen.
     * We will navigate to next screen.
     */
    private fun checkPermissionOnEveryResume() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                if (permissionsHelper.hasReadStoragePermission()) {
                    navigateToFoldersScreen()
                }
            }
        }
    }

    override fun onEventReceived(event: Int) {
        if (event == ON_ALLOW) {
            permissionsHelper.requestStorageReadPermission()
        }
    }

}