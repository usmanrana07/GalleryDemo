package com.gallerydemo.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {


    fun onAllowClicked() {
        Log.d("usm_test_allow", "onAllowClicked")
    }

}