package com.example.foodcompanion


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.foodcompanion.*
import com.example.foodcompanion.screens.MainPage


object UserInformation {
    var institutionID: String = ""
    var patientID: String = ""
    var birthday: String = ""
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // create a LoginCalls object to call stuff in the ui
        setContent {
            Main()
            //MainPage()
        }
        val serverIntent = Intent(this, ServerService::class.java)
        startService(serverIntent)
    }

}



