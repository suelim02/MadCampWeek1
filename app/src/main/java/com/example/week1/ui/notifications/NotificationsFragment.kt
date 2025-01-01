package com.example.week1.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.week1.SharedViewModel
import com.example.week1.databinding.FragmentNotificationsBinding
import com.example.week1.ui.dashboard.ImageDB

class NotificationsFragment : Fragment() {
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var notificationsViewModel: NotificationsViewModel
    private lateinit var adapter: NotificationPostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        notificationsViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)

        // 데이터 초기화
        val data = notificationsViewModel.posts.value ?: emptyList()
        // DBHelper 초기화
        val dbHelper = ImageDB(requireContext())

        // 어댑터 초기화
        adapter = NotificationPostAdapter(requireContext(), dbHelper, data, sharedViewModel)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // ListView 설정
        val listView: ListView = binding.listView
        listView.adapter = adapter

        val win = sharedViewModel.victoryCount.value ?: 0
        val total = sharedViewModel.totalImageCount.value ?: 0
        binding.winStatitics.text = "${total}전 ${win}승"
        if(total != 0)
            binding.winProbability.text = String.format("%.1f %%", win.toDouble() / total * 100)
        else    binding.winProbability.text = "0%"

        // ViewModel의 데이터를 관찰
        notificationsViewModel.posts.observe(viewLifecycleOwner) { postList ->
            adapter.notifyDataSetChanged()
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.totalImageCount.observe(viewLifecycleOwner) { total ->
            // 총 개수를 UI에 반영
        }

        sharedViewModel.victoryCount.observe(viewLifecycleOwner) { victories ->
            // victory 개수를 UI에 반영
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

