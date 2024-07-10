package com.example.foodcompanion.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
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
fun LoginPage(
    pageToNavigateTo: () -> Unit = {},
    modifier: Modifier = Modifier) {
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

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Date of Birth", fontSize = 18.sp,
            modifier = Modifier.align(Alignment.Start).padding(horizontal = 56.dp, vertical = 8.dp))
        //date of birth
        Row (modifier = Modifier.width(301.dp)){
            val birthdayMod = Modifier
                .weight(1f)
                .padding(10.dp)
                .height(30.dp)
            var year by remember { mutableStateOf("") }
            TextField(
                value = year,
                onValueChange = { year = it },
                label = { Text("(YYYY)") },
                singleLine = true,
                modifier = Modifier
                    .weight(1.2f)
                    .padding(10.dp)
                    .height((30.dp)),
            )
            var month by remember { mutableStateOf("") }
            TextField(
                value = month,
                onValueChange = { month = it },
                label = { Text("(MM)") },
                singleLine = true,
                modifier = birthdayMod,
            )
            var day by remember { mutableStateOf("") }
            TextField(
                value = day,
                onValueChange = { day = it },
                label = { Text("(DD)") },
                singleLine = true,
                modifier = birthdayMod,
            )
        }
        Spacer(modifier = Modifier.height(spaceHeight))

        //This is the button
        Button(onClick = {
            //verifyID(institutionID, patientID, year+month+day, pageToNavigateTo)
            pageToNavigateTo()
        }) {
            Text("Log In")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TextFieldPreview() {
    LoginPage()
}