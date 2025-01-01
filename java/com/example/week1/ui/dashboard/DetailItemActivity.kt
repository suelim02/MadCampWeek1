package com.example.week1.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.week1.R
import com.example.week1.databinding.ActivityDetailItemBinding

class DetailItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailItemBinding
    private var position: Int = -1
    private var description: String? = null
    private var date: String? = null
    private var type: String? = null
        set(value) {
            Log.d("DetailItemActivity", "Type updated: $value")
            field = value
        }
    private var victory: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailed_screen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 전달받은 데이터
        val imageUri = Uri.parse(intent.getStringExtra("imageUri"))
        description = intent.getStringExtra("description")
        date = intent.getStringExtra("date")
        type = intent.getStringExtra("type")
        victory = intent.getBooleanExtra("victory", false)
        position = intent.getIntExtra("position", -1)
        Log.d("DetailItemActivity", "Received Data: imageUri=$imageUri, description=$description, date=$date, position=$position")
        // 데이터 초기화
        binding.detailedImage.setImageURI(imageUri)
        binding.detailedDescription.text = description
        binding.detailedDate.text = "날짜: $date"
        binding.detailedType.text = "종목: $type"
        if(victory == true)
            binding.detailedVictory.text = "승요 ㅎㅎ"
        else
            binding.detailedVictory.text = "패귀 ㅠㅠ"

        // 수정 버튼 클릭
        binding.btnModify.setOnClickListener {
            val editDialog = EditTextDialogFragment(
                initialTitle = description,
                initialDate = date
            ) { updatedDescription, updatedDate, selectedCategory, checked->
                if (updatedDescription != null && updatedDate != null) {
                    // 데이터 업데이트
                    description = updatedDescription
                    date = updatedDate
                    type = selectedCategory
                    victory = checked
                    binding.detailedDescription.text = description
                    binding.detailedDate.text = "종목: $date"
                    binding.detailedType.text = "날짜: $type"
                    Log.d("DetailItemActivity", "Updated values - Type: $type, Victory: $victory")
                    if(victory == true)
                        binding.detailedVictory.text = "승요 ㅎㅎ"
                    else
                        binding.detailedVictory.text = "패귀 ㅠㅠ"
                }
            }
            editDialog.show(supportFragmentManager, "EditDialog")
        }

        // 완료 버튼 클릭
        binding.btnDone.setOnClickListener {
            Log.d("DetailItemActivity", "Before setting result - Description: $description, Date: $date, Type: $type, Victory: $victory")
            // 수정된 데이터 전달 후 종료
            val resultExtras = Bundle().apply {
                putString("updatedDescription", description)
                putString("updatedDate", date)
                putInt("position", position)
                putString("updatedType", type)
                putBoolean("updatedVictory", victory)
            }

            // Intent에 Bundle 추가
            val resultintent = Intent().apply {
                putExtras(resultExtras)
            }
            Log.d("DetailItemActivity", "After setting result: Intent extras: ${resultintent.extras}")
            Log.d("DetailItemActivity", "Type directly in Intent: ${resultintent.getStringExtra("updatedType")}")
            setResult(RESULT_OK, resultintent)
            finish()
        }
    }
}