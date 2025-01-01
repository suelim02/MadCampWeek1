package com.example.week1.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NotificationsViewModel : ViewModel() {
    private val _posts = MutableLiveData<List<NotificationPost>>()
    val posts: LiveData<List<NotificationPost>> = _posts

    init {
        // 초기 데이터 설정
        _posts.value = listOf(
            NotificationPost("1. 직관 5회하기!"),
            NotificationPost("2. 축구 배구 농구 야구 직관하기!"),
            NotificationPost("3. 승요 5회하기!")
        )
    }

    fun addPost(post: NotificationPost) {
        // 현재 리스트를 MutableList로 변환
        val currentList = _posts.value?.toMutableList() ?: mutableListOf()
        currentList.add(post)
        _posts.value = currentList
    }
}
