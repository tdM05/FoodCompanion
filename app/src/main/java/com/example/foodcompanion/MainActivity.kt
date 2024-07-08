package com.example.foodcompanion


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uicommunicator.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // create a LoginCalls object to call stuff in the ui
        setContent {
            LoginPage()
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

@Composable
fun LoginPage(modifier: Modifier = Modifier) {
    val spaceHeight = 26.dp
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(100.dp))
        // This is the title
        Text(text = "Food Companion", fontSize = 60.sp, lineHeight = 60.sp, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(spaceHeight))

        //patient ID
        var patientID by remember { mutableStateOf("") }
        TextField(
            value = patientID,
            onValueChange = { patientID = it },
            label = { Text("patient ID") },
            modifier = modifier,
        )

        Spacer(modifier = Modifier.height(spaceHeight))

        //institutionID
        var institutionID by remember { mutableStateOf("") }
        TextField(
            value = institutionID,
            onValueChange = { institutionID = it },
            label = { Text("Institution ID") },
            modifier = modifier,
        )

        Spacer(modifier = Modifier.height(spaceHeight))

        //This is the button
        Button(onClick = { verifyID(patientID, institutionID) }) {
            Text("Log In")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TextFieldPreview() {
    LoginPage()
}

