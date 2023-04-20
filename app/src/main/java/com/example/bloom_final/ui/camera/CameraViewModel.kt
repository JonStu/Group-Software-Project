package com.example.bloom_final.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is where the camera function will go once its done." + "Camera X is the Android API which will be used"
    }
    val text: LiveData<String> = _text
}