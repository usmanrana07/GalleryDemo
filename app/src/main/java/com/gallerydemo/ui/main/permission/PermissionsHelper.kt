package com.gallerydemo.ui.main.permission

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.gallerydemo.R
import com.gallerydemo.utils.PermissionsResultCallback
import com.gallerydemo.utils.printLog

/**
 * This class is to check and handle the required permissions
 * or to redirect user to settings screen when needed
 */
class PermissionsHelper(private val fragment: Fragment) : LifecycleEventObserver {
    private var lifecycle: Lifecycle? = null
    private var permissionsResultCallback: PermissionsResultCallback? = null
    private val permissionRequestLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionsResultCallback?.invoke(permissions)
        permissionsResultCallback = null
    }

    private fun launchPermissionForResult(
        intent: Array<String>,
        resultCallback: PermissionsResultCallback?
    ) {
        this.permissionsResultCallback = resultCallback
        permissionRequestLauncher.launch(intent)
    }

    fun setLifecycleOwner(owner: LifecycleOwner) {
        lifecycle?.removeObserver(this)
        lifecycle = owner.lifecycle.also {
            it.addObserver(this)
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {

        if (event.targetState == Lifecycle.State.DESTROYED) {
            permissionsResultCallback = null
            permissionRequestLauncher.unregister()
        }
    }

    fun requestStorageReadPermission() {
        val permissionsToRequest = galleryReadPermissions()
        launchPermissionForResult(permissionsToRequest) { permissions ->
            onPermissionRequestResponseReceived(permissions, permissionsToRequest)
        }
    }

    fun hasReadStoragePermission(): Boolean {
        return galleryReadPermissions().any {
            ActivityCompat.checkSelfPermission(
                fragment.requireContext(), it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun galleryReadPermissions(): Array<String> {
        return if (isGranularPermissionsSupport()) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun onPermissionRequestResponseReceived(
        permissions: Map<String, Boolean>,
        requestedPermissions: Array<String>
    ) {
        when {
            permissions.none { !it.value } -> {
                printLog("usm_gallery_permission", "granted: $permissions")
            }
            else -> {
                val isNeverAskState = requestedPermissions.any { isNeverAskPermissionSet(it) }
                printLog(
                    "usm_gallery_permission",
                    "isNeverAskState: $isNeverAskState ,permissions: $permissions"
                )
                if (isNeverAskState) {
                    showPermissionSettingsConfirmationDialog()
                }
            }
        }
    }

    private fun isGranularPermissionsSupport() =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    private fun isNeverAskPermissionSet(permission: String) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fragment.shouldShowRequestPermissionRationale(permission).not()
        } else {
            false
        }

    private fun openPermissionSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", fragment.requireContext().packageName, null)
        intent.data = uri
        fragment.startActivity(intent)
    }

    private fun showPermissionSettingsConfirmationDialog() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(fragment.requireContext())
            .setTitle(fragment.getString(R.string.app_name))
            .setMessage(fragment.getString(R.string.message_open_permissions_setting))
            .setPositiveButton(R.string.settings) { dialog, _ ->
                openPermissionSettings()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

}