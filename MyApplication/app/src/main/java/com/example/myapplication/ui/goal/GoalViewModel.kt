package com.example.myapplication.ui.goal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GoalViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is goal Fragment"
    }
    val text: LiveData<String> = _text
}