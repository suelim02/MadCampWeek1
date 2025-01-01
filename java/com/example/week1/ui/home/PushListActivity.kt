package com.example.week1.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.SearchView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.week1.R
import com.example.week1.home.Myadapter_addpage
import org.json.JSONArray


class pushListActivity : AppCompatActivity() {

    private lateinit var adapter: Myadapter_addpage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_push_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dbHelper = allListDB(this)
        val jsonData = JSONArray(loadTeamsFromJson(this, "TeamDataBase.json"))
        dbHelper.saveJsonToDatabase(jsonData)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_alllist)
        val teamList = dbHelper.getAllTeams()
        if (teamList.isEmpty()) {
            Log.e("pushListActivity", "Team list is empty!")
        } else {
            Log.d("pushListActivity", "Loaded teams: $teamList")
        }
        val searchList: SearchView = findViewById(R.id.search_alllist)
        val spinner: Spinner = findViewById(R.id.sort_search)
        var selectedField = "name" // 기본 필드

        adapter = Myadapter_addpage(teamList)  { selectedTeam ->
            val resultIntent = Intent().apply {
                putExtra("selected_data", arrayListOf(selectedTeam))
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(divider)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedField = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedField = "name"
            }
        }
        searchList.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { adapter.filter(it, selectedField) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { adapter.filter(it, selectedField) }
                return true
            }
        })
    }
}