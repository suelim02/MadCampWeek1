package com.example.week1.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.week1.MainActivity
import com.example.week1.databinding.FragmentHomeBinding
import com.example.week1.ui.dashboard.ImageDB
import java.util.ArrayList

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var imagedbHelper: ImageDB // image DB
    private lateinit var favordbHelper: favorListDB // 즐찾 DB
    private lateinit var favorrecyclerView: RecyclerView
    private lateinit var adapter: Myadapter
    private lateinit var resultLauncher: ActivityResultLauncher<Intent> // 결과값 가져오기 위한 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favordbHelper = favorListDB(requireContext())
        imagedbHelper = ImageDB(requireContext())
        // 다른 activity(pushlistActicivy)에서 가져온 결과값 저장
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val selectedData = result.data?.getSerializableExtra("selected_data") as? ArrayList<Team>
                if (selectedData != null) {
                    favordbHelper.insertTeam(selectedData[0].name, selectedData[0].stadium,
                        selectedData[0].type, selectedData[0].location,
                        selectedData[0].seat, selectedData[0].ticket)
                    val favorList = favordbHelper.getFavorTeams().toMutableList()

                    adapter = Myadapter(favorList) { team ->
                        // 버튼 클릭 시 처리
                        favordbHelper.deleteTeam(team.name) // DB에서 팀 삭제
                        adapter.removeTeam(team) // 어댑터에서 팀 삭제 후 RecyclerView 갱신
                        Toast.makeText(requireContext(), "${team.name} 팀이 제외되었습니다.", Toast.LENGTH_SHORT).show()
                    }

                    favorrecyclerView.layoutManager = LinearLayoutManager(requireContext())
                    favorrecyclerView.adapter = adapter

                    val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
                    favorrecyclerView.addItemDecoration(divider)
                    Toast.makeText(requireContext(), "${selectedData[0].name} 팀이 추가되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        favorrecyclerView = binding.recyclerviewFavorlist
        val searchList = binding.searchFavorlist
        val favorList = favordbHelper.getFavorTeams().toMutableList()
        val spinner = binding.sortSearch
        var selectedField = "name" // 기본 필드

        adapter = Myadapter(favorList) { team ->
            // 버튼 클릭 시 처리
            favordbHelper.deleteTeam(team.name) // DB에서 팀 삭제
            adapter.removeTeam(team) // 어댑터에서 팀 삭제 후 RecyclerView 갱신
            adapter.filter("", "name")
            Toast.makeText(requireContext(), "${team.name} 팀이 제외되었습니다.", Toast.LENGTH_SHORT).show()
        }

        favorrecyclerView.layoutManager = LinearLayoutManager(requireContext())
        favorrecyclerView.adapter = adapter

        val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        favorrecyclerView.addItemDecoration(divider)

        // 초기 화면에서 승리직관, 총 직관 설정
        val totalCount = imagedbHelper.getAllImages().size
        val victoryCount = imagedbHelper.getAllImages().count { it.victory }
        (requireActivity() as MainActivity).sharedViewModel.updateCounts(totalCount, victoryCount)

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

        val btn_push_list: Button = binding.btnPlusList
        btn_push_list.setOnClickListener    {
            val intent = Intent(requireContext(), pushListActivity::class.java)
            resultLauncher.launch(intent)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}