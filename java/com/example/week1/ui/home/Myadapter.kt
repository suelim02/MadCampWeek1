package com.example.week1.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.week1.R

class Myadapter(private val teamList: MutableList<Team>,
                private val onButtonClick: (Team) -> Unit
) : RecyclerView.Adapter<Myadapter.MyViewHolder>() {

    private var filteredList: MutableList<Team> = teamList.toMutableList()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)    {
        val teamText: TextView = itemView.findViewById(R.id.team_name)
        val stadiumText: TextView = itemView.findViewById(R.id.team_stadium)
        val favButton: ImageButton = itemView.findViewById(R.id.btn_favor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_teamlist, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentTeam = filteredList[position]
        holder.teamText.text = currentTeam.name
        holder.stadiumText.text = currentTeam.stadium
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, TeaminfoActivity::class.java).apply {
                putExtra("team_name", currentTeam.name)
                putExtra("team_stadium", currentTeam.stadium)
                putExtra("team_type", currentTeam.type)
                putExtra("team_location", currentTeam.location)
                putExtra("team_seat", currentTeam.seat)
                putExtra("team_ticket", currentTeam.ticket)
            }
            context.startActivity(intent)
        }
        holder.favButton.setOnClickListener{
            val actualPosition = teamList.indexOf(currentTeam)
            if (actualPosition != -1) {
                teamList.removeAt(actualPosition)
                filter("", "") // 전체 리스트 재적용
                notifyDataSetChanged() // RecyclerView 강제 갱신
            }
            onButtonClick(currentTeam)
        }
    }

    override fun getItemCount() = filteredList.size

    fun removeTeam(team: Team) {
        val position = teamList.indexOf(team)
        if (position != -1) {
            filteredList.removeAt(position) // 리스트에서 제거
            notifyItemRemoved(position) // 해당 위치의 아이템을 RecyclerView에 알림
        }
    }

    fun filter(query: String, field: String) {
        filteredList.clear()
        if (query.isNullOrEmpty()) {
            filteredList.addAll(teamList) // 검색어가 없으면 전체 리스트 표시
        } else {
            filteredList.addAll(
                teamList.filter { team ->
                    when (field) {
                        "name" -> team.name.contains(query, ignoreCase = true) // 이름으로 검색
                        "stadium" -> team.stadium.contains(query, ignoreCase = true) // 경기장으로 검색
                        else -> false
                    }
                }
            )
        }
        notifyDataSetChanged()
    }
}