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

        val binding: ActivityDetailBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_detail)
        setSupportActionBar(binding.toolbar)

        // Get Intent Extras
        binding.included.fileName.text = intent.getStringExtra(Constants.KEY_REPOSITORY_NAME).toString()
        binding.included.statusTextView.text = intent.getStringExtra(Constants.KEY_STATUS).toString()

        // Ok Button On Click Listener
        binding.included.okButton.setOnClickListener {
            // Go to main activity
            val mainActivity = Intent(this, MainActivity::class.java)
            startActivity(mainActivity)
        }
    }
}
