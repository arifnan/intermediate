package com.example.submissionintermediate1.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submissionintermediate1.R
import com.example.submissionintermediate1.databinding.ActivityMainBinding
import com.example.submissionintermediate1.login.LoginActivity
import com.example.submissionintermediate1.data.response.StoryList
import com.example.submissionintermediate1.data.response.ViewModelFactory
import com.example.submissionintermediate1.ui.Tambah_Story.NewStoryActivity
import com.example.submissionintermediate1.ui.Tambah_Story.StoryAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStory.addItemDecoration(itemDecoration)

        binding.fabAddstory.setOnClickListener { moveToAddStory() }

        binding.logoutButton.setOnClickListener { clearSession() }

        viewModel.getSession().observe(this) { user ->
            Log.wtf("user session", "User Token ${user.token}")
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finishAffinity()
            }
        }
        setupData()
    }

    private fun setupData() {
        viewModel.getSession().observe(this) { user ->
            if (user.token.isNotBlank()) {
                processGetAllStories(user.token)
            }
        }
    }

    private fun clearSession() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.sign_out)
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.oke) { _, _ ->
                viewModel.deleteLogin()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()
    }

    private fun processGetAllStories(token: String) {
        viewModel.getStories(token).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.loading.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.loading.visibility = View.GONE
                        setListStory(result.data)
                    }

                    is Result.Error -> {
                        Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                    }
                }
            }
        }
    }

    private fun setListStory(listStory: StoryList?) {
        if (listStory != null) {
            val adapter = StoryAdapter()
            adapter.submitList(listStory.listStory)
            binding.rvStory.adapter = adapter
        }

    }

    private fun moveToAddStory() {
        startActivity(Intent(this, NewStoryActivity::class.java))
    }
}
