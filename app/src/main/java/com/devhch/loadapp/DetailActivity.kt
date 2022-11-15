package com.devhch.loadapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.devhch.loadapp.databinding.ActivityDetailBinding
import com.devhch.loadapp.utils.Constants

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get a reference to the binding object and inflate the fragment views.
        val binding: ActivityDetailBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_detail)

        // Set ToolBar
        setSupportActionBar(binding.toolbar)

        // Get Intent Extras
        binding.included.repositoryNameTextView.text =
            intent.getStringExtra(Constants.KEY_REPOSITORY_NAME)
        binding.included.statusTextView.text =
            intent.getStringExtra(Constants.KEY_STATUS)

        // Ok Button On Click Listener
        binding.included.okButton.setOnClickListener {
            // Go to main activity
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
