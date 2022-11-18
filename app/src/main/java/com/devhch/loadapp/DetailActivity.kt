package com.devhch.loadapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
        val repository = intent.getStringExtra(Constants.KEY_REPOSITORY_NAME)
        val status = intent.getStringExtra(Constants.KEY_STATUS)

        // Set Values
        binding.included.repositoryNameTextView.text = repository
        binding.included.statusTextView.text = status

        // Generate Text Color Depends on Status
        val color = if (status!!.contains("success")) R.color.colorPrimary else R.color.colorAccent
        binding.included.statusTextView.setTextColor(
            ContextCompat.getColor(
                applicationContext,
                color
            )
        )

        // Ok Button On Click Listener
        binding.included.okButton.setOnClickListener {
            // Go to main activity
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
