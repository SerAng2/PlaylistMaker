package com.example.my

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.my.databinding.ActivitySearchBinding
import androidx.core.widget.doOnTextChanged

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private var searchText: String = ""

    companion object {
        private const val SEARCH_TEXT_KEY = "search_text_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchText = savedInstanceState?.getString(SEARCH_TEXT_KEY) ?: ""

        with(binding) {
            searchEditText.setText(searchText)
            updateClearButtonVisibility(searchText.isNotEmpty())

            toolbar.setNavigationOnClickListener { finish() }
            setupSearchField()
        }
    }

    private fun setupSearchField() {
        with(binding) {
            searchEditText.requestFocus()
            showKeyboard()

            clearButton.setOnClickListener {
                searchEditText.text?.clear()
                hideKeyboard()
            }

            searchEditText.doOnTextChanged { text, _, _, _ ->
                searchText = text?.toString() ?: ""
                updateClearButtonVisibility(text?.isNotEmpty() == true)
            }
        }
    }

    private fun updateClearButtonVisibility(visible: Boolean) {
        binding.clearButton.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    private fun showKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }
}