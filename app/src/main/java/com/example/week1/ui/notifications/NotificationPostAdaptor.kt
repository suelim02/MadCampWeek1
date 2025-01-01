package com.example.week1.ui.notifications

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.week1.R
import com.example.week1.SharedViewModel
import com.example.week1.ui.dashboard.ImageDB

class NotificationPostAdapter(
    private val context: Context,
    private val dbHelper: ImageDB, // ImageDB 추가
    private val postList: List<NotificationPost>,
    private val sharedViewModel: SharedViewModel
) : BaseAdapter() {

    override fun getCount(): Int = postList.size
    override fun getItem(position: Int): Any = postList[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.notification_post, parent, false)

        val textTitle: TextView = view.findViewById(R.id.textTitle)
        val imageView: ImageView = view.findViewById(R.id.buttonAction)

        val post = postList[position]
        textTitle.text = post.title

        if(position == 0){
            // ImageDB에서 행(row) 개수 확인
            val rowCount = sharedViewModel.totalImageCount.value
            //val rowCount = dbHelper.getRowCount()
            if (rowCount != null && rowCount >= 5) {
                imageView.setImageResource(R.drawable.complete) // 5개 이상일 때
            } else {
                imageView.setImageResource(R.drawable.notyet) // 5개 미만일 때
            }
        }else if(position == 2){
            val rowCount = sharedViewModel.victoryCount.value
            //val rowCount = dbHelper.getRowCount()
            if (rowCount != null && rowCount >= 5) {
                imageView.setImageResource(R.drawable.complete) // 10개 이상일 때
            } else {
                imageView.setImageResource(R.drawable.notyet) // 10개 미만일 때
            }
        }


        return view
    }
}