package com.example.week1.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.week1.databinding.FragmentEditTextBinding

class EditTextFragment : Fragment() {

    private var _binding: FragmentEditTextBinding? = null
    private val binding get() = _binding!!
    private var position: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditTextBinding.inflate(inflater, container, false)

        val args = EditTextFragmentArgs.fromBundle(requireArguments())
        position = args.position
        binding.editText.setText(args.currentText) // 기존 텍스트 설정

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
