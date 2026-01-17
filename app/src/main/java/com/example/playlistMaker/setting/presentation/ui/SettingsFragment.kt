package com.example.playlistMaker.setting.presentation.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistMaker.common.presentation.AppTheme
import com.example.playlistMaker.databinding.FragmentSettingsBinding
import com.example.playlistMaker.setting.presentation.view_model.SettingViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel: SettingViewModel by viewModel()

    // используем ViewBinding, мы можем использовать его так же как и в Activity
    private var _binding: FragmentSettingsBinding? = null

    // создаём неизменяемую переменную, к которой можно будет обращаться без ?. Мы должны не забыть инициализировать _binding, до того как использовать
    private val binding get() = _binding!!

    @SuppressLint("ClickableViewAccessibility")
    // в момент вызова onCreateView создаётся View для Fragment, поэтому именно в этот момент мы инициализируем binding и настраиваем View-элементы
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()

        binding.themeSwitcher.isChecked =
            (requireContext().applicationContext as AppTheme).darkTheme

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (requireContext().applicationContext as AppTheme).switchTheme(checked)
        }
    }

    private fun setupViews() {

        binding.shareAppTV.setOnClickListener {
            viewModel.onShareAppClicked()
        }

        binding.contactSupportTV.setOnClickListener {
            viewModel.onContactSupportClicked()
        }

        binding.userAgreementTV.setOnClickListener {
            viewModel.onOpenUserAgreementClicked()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
