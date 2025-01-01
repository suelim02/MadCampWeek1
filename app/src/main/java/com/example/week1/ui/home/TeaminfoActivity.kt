package com.example.week1.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.week1.databinding.ActivityTeaminfoBinding
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.week1.R

class TeaminfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTeaminfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTeaminfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 전달받은 데이터 가져오기
        val teamName = intent.getStringExtra("team_name")
        val teamStadium = intent.getStringExtra("team_stadium")
        val teamType = intent.getStringExtra("team_type")
        val teamLocation = intent.getStringExtra("team_location")
        val teamSeat = intent.getStringExtra("team_seat")
        val teamTicket = intent.getStringExtra("team_ticket")

        val btn_link: Button = binding.btnLink
        btn_link.setOnClickListener {
            val url = teamTicket // 이동할 URL
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        val btn_map: Button = binding.btnMap
        btn_map.setOnClickListener {
            val addr = teamLocation // 이동할 URL
            val geoUri = Uri.parse("geo:0,0?q=$addr")
            val intent = Intent(Intent.ACTION_VIEW, geoUri)
            startActivity(intent)
        }

        // 데이터를 레이아웃에 반영
        binding.teamName.text = teamName
        binding.teamStadium.text = teamStadium
        binding.teamType.text = teamType
        binding.teamLocation.text = teamLocation
        binding.teamSeat.text = teamSeat
        //binding.teamTicket.text = teamTicket
    }
}