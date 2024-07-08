package com.example.foodcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        // This is the title
        Text(text = "Food Companion", fontSize = 60.sp, lineHeight = 60.sp, textAlign = TextAlign.Center)
        //These are input fields
        TextInputField("Institution ID", modifier = Modifier.padding(16.dp))
        TextInputField("Patient ID", modifier = Modifier.padding(16.dp))
        //This is the button
    }
}

@Composable
fun TextInputField(label: String, initialString: String = "", modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf(initialString) }
    TextField(
        value = text,
        onValueChange = { text = it },
        label = { Text(label) },
        modifier = modifier,
    )
}

@Composable
fun PatientID(modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }
    TextField(
        value = text,
        onValueChange = { text = it },
        label = { Text("Label") },
    )
}

@Preview(showBackground = true)
@Composable
fun TextFieldPreview() {
    LoginPage()
}