package com.example.week1.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.week1.R

class ImageListAdapter(
    private val imageList: MutableList<ImageDiary>,
    private val onItemClick: (ImageDiary) -> Unit // 클릭 이벤트 콜백 추가
) : RecyclerView.Adapter<ImageListAdapter.ImageViewHolder>() {

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.item_image)
        val textView: TextView = view.findViewById(R.id.item_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.imagediaryitem, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageDiary = imageList[position]
        holder.imageView.setImageURI(imageDiary.imageUri)
        holder.textView.text = imageDiary.description

        // 아이템 클릭 이벤트 설정
        holder.itemView.setOnClickListener {
            onItemClick(imageDiary) // 클릭 시 콜백 호출
        }
    }

    override fun getItemCount(): Int = imageList.size
}
