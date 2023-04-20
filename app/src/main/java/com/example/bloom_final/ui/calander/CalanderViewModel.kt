package com.example.bloom_final.ui.calander

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalanderViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is calander Fragment"
    }
    val text: LiveData<String> = _text
}