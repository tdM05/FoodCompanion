package com.example.foodcompanion.screens

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
import uicommunicator.verifyID

@Composable
fun LoginPage
(
    pageToNavigateTo: () -> Unit = {},
    modifier: Modifier = Modifier
)
{
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
            singleLine = true,
            modifier = modifier
        )

        Spacer(modifier = Modifier.height(spaceHeight))

        //institutionID
        var institutionID by remember { mutableStateOf("") }
        TextField(
            value = institutionID,
            onValueChange = { institutionID = it },
            label = { Text("Institution ID") },
            singleLine = true,
            modifier = modifier,
        )

        Spacer(modifier = Modifier.height(spaceHeight))

        //date of birth
        var birthday by remember { mutableStateOf("") }
        TextField(
            value = birthday,
            onValueChange = { birthday = it },
            label = { Text("Birthday (YYYY/MM/DD)") },
            singleLine = true,
            modifier = modifier,
        )
        Spacer(modifier = Modifier.height(spaceHeight))

        //This is the button
        Button(onClick = {
            if (verifyID(institutionID, patientID, birthday, pageToNavigateTo) ){
                pageToNavigateTo()
            }
        }
        ) {
            Text("Log In")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TextFieldPreview() {
    LoginPage()
}