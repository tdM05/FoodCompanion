package com.example.foodcompanion
import android.content.Intent
import com.example.foodcompanion.ServerService
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.foodcompanion.ui.theme.FoodCompanionTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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

fun verifyID(patientID: String, institutionID: String = ""){
    Log.d("debug", "patientID: " + patientID + ", " + "institutionID: " +
    institutionID)
}