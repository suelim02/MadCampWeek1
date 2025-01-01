package com.example.week1.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.week1.MainActivity
import com.example.week1.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var imagedbHelper: ImageDB // 즐찾 DB
    private lateinit var imagerecyclerView: RecyclerView
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var imageList: MutableList<ImageDiary> // RecyclerView 데이터 리스트
    //private val imageList = ArrayList<ImageDiary>() // RecyclerView 데이터 리스트
    private lateinit var adapter: ImageListAdapter // RecyclerView 어댑터

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagedbHelper = ImageDB(requireContext())
        // ActivityResultLauncher 초기화
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                imageUri?.let {
                    requireContext().contentResolver.takePersistableUriPermission(
                        imageUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    // 다이얼로그를 띄워 텍스트와 날짜 입력받기
                    val inputTextDialog = EditTextDialogFragment(initialTitle = null, // 새 데이터이므로 기존 제목은 null
                        initialDate = null) {inputText, inputTextDate, spinnerValue, ischecked ->
                        val newItem = ImageDiary(
                            imageUri = it,
                            description = inputText ?: "Default Text",
                            date = inputTextDate ?: "Default Date",// 기본 날짜
                            type = spinnerValue,
                            victory = ischecked
                        )
                        imagedbHelper.insertImage(newItem.imageUri, newItem.description, newItem.date, newItem.type, newItem.victory)

                        // 데이터를 리스트 맨 위에 추가
                        //imageList.add(0, newItem)
                        imageList.clear()
                        imageList.addAll(imagedbHelper.getAllImages())
                        adapter.notifyDataSetChanged()

                        // 어댑터에 삽입된 데이터 알리기
                        //adapter.notifyItemInserted(0)

                        // RecyclerView를 맨 위로 스크롤
                        binding.recyclerView.scrollToPosition(0)
                        val totalCount = imagedbHelper.getAllImages().size
                        val victoryCount = imagedbHelper.getAllImages().count { it.victory }
                        (requireActivity() as MainActivity).sharedViewModel.updateCounts(totalCount, victoryCount)
                    }
                    inputTextDialog.show(parentFragmentManager, "EditTextDialog")
                }
            }
        }
        //Log.d("DashboardFragment", "Result launcher registered")
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            //Log.d("DashboardFragment", "Result launcher registered")
            if (result.resultCode == Activity.RESULT_OK) {
                //Log.d("DashboardFragment", "Result launcher registered")
                val data = result.data
                Log.d("DashboardFragment", "Intent received: $data")
                Log.d("DashboardFragment", "Extras: ${data?.extras}")

                val position = data?.getIntExtra("position", -1) ?: return@registerForActivityResult
                val updatedDescription = data.getStringExtra("updatedDescription") ?: "Default Description"
                val updatedDate = data.getStringExtra("updatedDate") ?: "Default Date"
                val updatedType = data.getStringExtra("updatedType") ?: "Unknown"
                val updatedVictory = data.getBooleanExtra("updatedVictory", false)

                Log.d("DashboardFragment", "Position: $position")
                Log.d("DashboardFragment", "Updated Description: $updatedDescription")
                Log.d("DashboardFragment", "Updated Date: $updatedDate")
                Log.d("DashboardFragment", "Updated Type: $updatedType")
                Log.d("DashboardFragment", "Updated Victory: $updatedVictory")

                // Ensure all data is valid before processing
                if (position == -1 || updatedDescription == null || updatedDate == null || updatedType == null) {
                    Log.e("DashboardFragment", "Invalid data received from DetailItemActivity")
                    return@registerForActivityResult
                }

                // DB 및 RecyclerView 업데이트
                val updatedItem = imageList[position]
                val prevdescription = updatedItem.description
                val prevdate = updatedItem.date
                updatedItem.description = updatedDescription
                updatedItem.date = updatedDate
                updatedItem.type = updatedType
                updatedItem.victory = updatedVictory

                imagedbHelper.updateImage(updatedItem, prevdescription, prevdate)
                adapter.notifyItemChanged(position)

                val totalCount = imagedbHelper.getAllImages().size
                val victoryCount = imagedbHelper.getAllImages().count { it.victory }
                (requireActivity() as MainActivity).sharedViewModel.updateCounts(totalCount, victoryCount)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        imagedbHelper = ImageDB(requireContext())
        imagerecyclerView = binding.recyclerView
        imageList = imagedbHelper.getAllImages().toMutableList()
        // RecyclerView 초기화
        adapter = ImageListAdapter(imageList) { selectedItem ->
            // 아이템 클릭 시 세부화면으로 이동
            val intent = Intent(requireContext(), DetailItemActivity::class.java).apply {
                putExtra("imageUri", selectedItem.imageUri.toString())
                putExtra("description", selectedItem.description)
                putExtra("date", selectedItem.date)
                putExtra("type", selectedItem.type)
                putExtra("victory", selectedItem.victory)
                putExtra("position", imageList.indexOf(selectedItem)) // 아이템 위치 전달
            }
            Log.d("DashboardFragment", "Sending data to DetailItemActivity: " +
                    "imageUri=${selectedItem.imageUri}, description=${selectedItem.description}, date=${selectedItem.date}")
            resultLauncher.launch(intent)
        }
//        }
        imagerecyclerView.layoutManager = LinearLayoutManager(requireContext())
        imagerecyclerView.adapter = adapter

        val totalCount = imagedbHelper.getAllImages().size
        val victoryCount = imagedbHelper.getAllImages().count { it.victory }

        (requireActivity() as MainActivity).sharedViewModel.updateCounts(totalCount, victoryCount)

        // 버튼 클릭 리스너 설정
        binding.buttonGallery.setOnClickListener {
            loadImage()
        }

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Triple<Int, String, String>>("updatedData")
            ?.observe(viewLifecycleOwner) { updatedData ->
                val (position, updatedText, updatedDate) = updatedData
                imageList[position].description = updatedText
                imageList[position].date = updatedDate // 날짜 업데이트
                adapter.notifyItemChanged(position) // RecyclerView 갱신
            }
    }

    private fun loadImage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        galleryLauncher.launch(intent)

    }

    companion object {
        private const val REQUEST_DETAIL = 1
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
