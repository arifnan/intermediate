package com.example.submissionintermediate1.ui.Tambah_Story

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.submissionintermediate1.R
import com.example.submissionintermediate1.data.response.Result
import com.example.submissionintermediate1.databinding.ActivityNewStoryBinding
import com.example.submissionintermediate1.main.MainViewModel
import com.example.submissionintermediate1.data.response.ViewModelFactory
import com.example.submissionintermediate1.util.getImageUri
import com.example.submissionintermediate1.util.reduceFileImage
import com.example.submissionintermediate1.util.uriToFile

class NewStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewStoryBinding
    private var currentImageUri: Uri? = null

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.elevation = 0f
        supportActionBar?.setTitle(R.string.upload_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.buttonAdd.setOnClickListener { uploadImage() }
    }

    private fun startGallery(){
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private var launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null){
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No Media Selected")
        }
    }

    private fun showImage(){
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivItemImage.setImageURI(it)
        }
    }

    private fun startCamera(){
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.edAddDescription.text.toString()
            showLoading(true)
            viewModel.getSession().observe(this) { user ->
                viewModel.addNewStory(user.token, description, imageFile).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                showLoading(true)
                                binding.buttonAdd.isEnabled = false
                                binding.btnGallery.isEnabled = false
                            }

                            is Result.Success -> {
                                showLoading(false)
                                binding.buttonAdd.isEnabled = true
                                binding.btnGallery.isEnabled = true
                                showToast(getString(R.string.add_new_story_success))
                                finish()
                            }

                            is Error -> {
                                showLoading(false)
                                binding.buttonAdd.isEnabled = true
                                binding.btnGallery.isEnabled = true
                                showToast(getString(R.string.add_new_story_failed))
                            }

                            else -> {}
                        }
                    }
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }
    private fun showLoading(isLoading: Boolean) {
        binding.loading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}