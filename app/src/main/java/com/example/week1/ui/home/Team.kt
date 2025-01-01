package com.example.week1.ui.home

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.io.Serializable

data class Team(
    val name: String,
    val stadium: String,
    val type: String,
    val location: String,
    val seat: String,
    val ticket: String
) : Serializable

fun loadTeamsFromJson(context: Context, fileName: String): String {
    return try {
        val inputStream = context.assets.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        String(buffer, Charsets.UTF_8)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}