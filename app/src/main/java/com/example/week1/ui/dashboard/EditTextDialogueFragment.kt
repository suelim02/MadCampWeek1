package com.example.week1.ui.dashboard

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.DialogFragment
import com.example.week1.R
import java.util.Calendar

class EditTextDialogFragment(
    private val initialTitle: String?, // 기존 제목
    private val initialDate: String?, // 기존 날짜
    private val onTextEntered: (String?, String?, String, Boolean) -> Unit // 콜백 함수
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_edit_text, null)

        val titleInput = view.findViewById<EditText>(R.id.editText)
        val dateInput = view.findViewById<EditText>(R.id.editTextDate)
        val spinner = view.findViewById<Spinner>(R.id.spinner_type) // Spinner 추가
        val switch = view.findViewById<SwitchCompat>(R.id.switch_victory) // Switch 추가

        // 기존 데이터 초기화
        titleInput.setText(initialTitle) // 제목 초기화
        dateInput.setText(initialDate)   // 날짜 초기화

        dateInput.isFocusable = false // 포커스 비활성화
        dateInput.isClickable = true // 클릭 가능
        dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // DatePickerDialog 생성 및 표시
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                // 선택된 날짜를 EditText에 설정
                val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                dateInput.setText(formattedDate)
            }, year, month, day).show()
        }

        // Spinner 설정
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.filter_type, // strings.xml에서 정의된 배열
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        builder.setView(view)
            .setTitle("Edit Entry")
            .setPositiveButton("OK") { _, _ ->
                val title = titleInput.text.toString()
                val date = dateInput.text.toString()
                val spinnerValue = spinner.selectedItem.toString()
                val isChecked = switch.isChecked
                Log.d("EditTextDialogFragment", "Title: $title, Date: $date, Spinner: $spinnerValue, Switch: $isChecked")
                onTextEntered(title, date, spinnerValue, isChecked)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        return builder.create()
    }
}

