package com.example.foodcompanion.screens

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uicommunicator.verifyID

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage
(
    pageToNavigateTo: () -> Unit = {},
    modifier: Modifier = Modifier
)
{
    val pattern = remember { Regex("^\\d+\$") }
    val spaceHeight = 28.dp
    val textPadding = 24.dp
    Box(Modifier.background(color = Color(0xFFFFFFFF))) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            // This is the title
            Text(
                text = "Food Companion",
                fontSize = 60.sp,
                lineHeight = 60.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFF1C5D99)
            )

            Spacer(modifier = Modifier.height(spaceHeight))

            val textFieldColor = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFBBCDE5),
                unfocusedContainerColor = Color(0xFFBBCDE5),
                cursorColor = Color(0xFF639FAB),
                focusedIndicatorColor = Color(0xFF639FAB),
                unfocusedIndicatorColor = Color(0xFF639FAB)
            )
            val textLabelColor = Color(0xFFFFFFFF)
            val textWidth = 284.dp
            val textHeight = 30.dp
            //patient ID
            var patientID by remember { mutableStateOf("") }
            TextField(
                value = patientID,
                onValueChange = {
                    patientID = it
                                },
                label = { Text("Patient ID", color = textLabelColor) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = textPadding),
                colors = textFieldColor,
                textStyle = LocalTextStyle.current.copy(color = Color(0xFF639FAB))
            )

            Spacer(modifier = Modifier.height(spaceHeight))

            //institutionID
            var institutionID by remember { mutableStateOf("") }
            TextField(
                value = institutionID,
                onValueChange = {
                    if (it.isEmpty() || it.matches(pattern)) {
                        institutionID = it
                    }
                                },
                label = { Text("Institution ID", color = textLabelColor) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = textPadding),
                colors = textFieldColor,
                textStyle = LocalTextStyle.current.copy(color = Color(0xFF639FAB))
            )

            Spacer(modifier = Modifier.height(spaceHeight))

            //date of birth
            var year by remember { mutableStateOf("") }
            var month by remember { mutableStateOf("") }
            var day by remember { mutableStateOf("") }
            val biMod = Modifier.weight(1f)
            val smallMod = Modifier.weight(0.75f)
            Text(text = "Date of Birth",
                fontSize = 20.sp,
                color = Color(0xFF1C5D99),
                modifier = Modifier.align(Alignment.Start).padding(horizontal = textPadding)
            )
            Row (
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = textPadding)
            ){
                TextField(
                    value = year,
                    onValueChange = {
                        if ((it.isEmpty() || it.matches(pattern))&&
                            it.length <= 4) {
                            val intIt = it.toIntOrNull()
                            year = if (intIt == null) {
                                it
                            } else if (intIt <= 2024){
                                it
                            } else {
                                "2024"
                            }
                        }
                                    },
                    label = { Text("(YYYY)", color = textLabelColor) },
                    singleLine = true,
                    modifier = biMod.padding(end = 16.dp),
                    colors = textFieldColor,
                    textStyle = LocalTextStyle.current.copy(color = Color(0xFF639FAB))
                )
                TextField(
                    value = month,
                    onValueChange = {
                        if ((it.isEmpty() || it.matches(pattern))&&
                            it.length <= 2) {
                            val intIt = it.toIntOrNull()
                            month = if (intIt == null) {
                                it
                            } else if (intIt <= 12) {
                                it
                            } else{
                                "12"
                            }
                        }
                    },
                    label = {
                        Text("(MM)", color = textLabelColor)
                            },
                    singleLine = true,
                    modifier = smallMod.padding(end = 8.dp),
                    colors = textFieldColor,
                    textStyle = LocalTextStyle.current.copy(color = Color(0xFF639FAB))
                )
                TextField(
                    value = day,
                    onValueChange = {
                        if ((it.isEmpty() || it.matches(pattern))&&
                            it.length <= 2) {
                            val intIt = it.toIntOrNull()
                            day = if (intIt == null) {
                                it
                            } else if (intIt <= 31) {
                                it
                            } else{
                                "31"
                            }
                        }
                                    },
                    label = { Text("(DD)", color = textLabelColor) },
                    singleLine = true,
                    modifier = smallMod.padding(start = 8.dp),
                    colors = textFieldColor,
                    textStyle = LocalTextStyle.current.copy(color = Color(0xFF639FAB))
                )
            }
            Spacer(modifier = Modifier.height(spaceHeight))

            //This is the button
            val context = LocalContext.current
            val birthday: String = year+month+day
            Log.d("birthday", birthday)
            Button(
                onClick = {
                    if (verifyID(institutionID, patientID, "20050103", pageToNavigateTo)) {
                        pageToNavigateTo()
                    } else {
                        Toast.makeText(
                            context,
                            "Log in failed",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                },
                colors = ButtonColors(Color(0xFF222222), Color(0xFFFFFFFF), Color.Blue, Color.Green)
            ) {
                Text("Log In")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun TextFieldPreview() {
    LoginPage()
}