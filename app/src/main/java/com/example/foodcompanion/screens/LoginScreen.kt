package com.example.foodcompanion.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uicommunicator.verifyID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage
(
    pageToNavigateTo: () -> Unit = {},
    modifier: Modifier = Modifier
)
{
    val spaceHeight = 26.dp
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
            //patient ID
            var patientID by remember { mutableStateOf("") }
            TextField(
                value = patientID,
                onValueChange = { patientID = it },
                label = { Text("patient ID", color = textLabelColor) },
                singleLine = true,
                modifier = modifier,
                colors = textFieldColor,
                textStyle = LocalTextStyle.current.copy(color = Color(0xFF639FAB))
            )

            Spacer(modifier = Modifier.height(spaceHeight))

            //institutionID
            var institutionID by remember { mutableStateOf("") }
            TextField(
                value = institutionID,
                onValueChange = { institutionID = it },
                label = { Text("Institution ID", color = textLabelColor) },
                singleLine = true,
                modifier = modifier,
                colors = textFieldColor,
                textStyle = LocalTextStyle.current.copy(color = Color(0xFF639FAB))
            )

            Spacer(modifier = Modifier.height(spaceHeight))

            //date of birth
            var birthday by remember { mutableStateOf("") }
            TextField(
                value = birthday,
                onValueChange = { birthday = it },
                label = { Text("Birthday (YYYYMMDD)", color = textLabelColor) },
                singleLine = true,
                modifier = modifier,
                colors = textFieldColor,
                textStyle = LocalTextStyle.current.copy(color = Color(0xFF639FAB))
            )
            Spacer(modifier = Modifier.height(spaceHeight))

            //This is the button
            val context = LocalContext.current
            Button(
                onClick = {
                    if (verifyID(institutionID, patientID, birthday, pageToNavigateTo)) {
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

@Preview(showBackground = true)
@Composable
fun TextFieldPreview() {
    LoginPage()
}