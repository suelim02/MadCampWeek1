package com.example.week1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _totalImageCount = MutableLiveData<Int>()
    val totalImageCount: LiveData<Int> get() = _totalImageCount

    private val _victoryCount = MutableLiveData<Int>()
    val victoryCount: LiveData<Int> get() = _victoryCount

    fun updateCounts(total: Int, victories: Int) {
        _totalImageCount.value = total
        _victoryCount.value = victories
    }
}