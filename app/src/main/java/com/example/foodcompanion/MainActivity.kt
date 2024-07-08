package com.example.foodcompanion


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.foodcompanion.Main




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // create a LoginCalls object to call stuff in the ui
        setContent {
            Main()
        }
        val serverIntent = Intent(this, ServerService::class.java)
        startService(serverIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop the server service
        val serverIntent = Intent(this, ServerService::class.java)
        stopService(serverIntent)
    }
}



