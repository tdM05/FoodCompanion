package com.example.foodcompanion.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodcompanion.R

object Notification {
    var message: MutableList<String> = mutableListOf("test1", "test2asdfasdfasdfsadfasdfasdfasdfasdfasdf")
}

@Composable
fun MainPage(
    onNextButtonClicked: () -> Unit = {},
) {

    Column {
        Spacer(modifier = Modifier.height(40.dp))
        // header
        Row {
            // profile
            ExpandableIcon(
                painter = painterResource(id = R.drawable.account_circle_24dp_5f6368_fill0_wght400_grad0_opsz24),
                menuList = mutableListOf(
                    Pair("Profile Information"){
                        Log.d("debug", "profile information")
                    },
                    Pair("Sign Out"){
                        Log.d("debug", "signed out")
                    }
                ),
                iconDescription = "Profile"
            )
            Spacer(modifier = Modifier.weight(1f))
            // notifications
            var menuList = mutableListOf<Pair<String, () -> Unit>>()
            for (i in 0..Notification.message.size - 1) {
                menuList.add(Pair(Notification.message[i]) {})
            }
            ExpandableIcon(
                painter = painterResource(id = R.drawable.notifications_24dp_5f6368_fill0_wght400_grad0_opsz24),
                menuList = menuList,
                iconDescription = "Profile",
                clickable = false
            )
        }
        Column (
            modifier = Modifier.verticalScroll(rememberScrollState())
        ){
            Text(
                text = "Add Food",
                fontSize = 60.sp,
                modifier = Modifier.padding(16.dp)
            )

            val columnModifier = Modifier.size(100.dp).weight(10f, fill = true).padding(1.dp)
            val iconModifier = Modifier.size(1000.dp).align(Alignment.CenterHorizontally)
            Row (horizontalArrangement = Arrangement.Center){
                Column (modifier = columnModifier){
                    Text("Breakfast", modifier = Modifier.align(Alignment.CenterHorizontally))
                    IconButton(
                        onClick = { /*TODO*/ },
                        modifier = iconModifier
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.breakfast_image),
                            contentDescription = "breakfast",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Column (modifier = columnModifier){
                    Text("Breakfast", modifier = Modifier.align(Alignment.CenterHorizontally))
                    IconButton(
                        onClick = { /*TODO*/ },
                        modifier = iconModifier
                        ) {
                        Image(
                            painter = painterResource(id = R.drawable.breakfast_image),
                            contentDescription = "breakfast",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Column (modifier = columnModifier){
                    Text("Breakfast", modifier = Modifier.align(Alignment.CenterHorizontally))
                    IconButton(
                        onClick = { /*TODO*/ },
                        modifier = iconModifier
                        ) {
                        Image(
                            painter = painterResource(id = R.drawable.breakfast_image),
                            contentDescription = "breakfast",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SignOut(){
    Log.d("debug", "signed out")
}

@Composable
fun ExpandableIcon(
    painter: Painter,
    menuList: MutableList<Pair<String, () -> Unit>>,
    iconDescription: String,
    clickable: Boolean = true,
    modifier: Modifier = Modifier
){
    /*
    menuList[0] contains the item name and menuList[1] contains the function to be called
    * */
    var expandedBool by remember {
        mutableStateOf(false)
    }
    Box {
        IconButton(onClick = { expandedBool = !expandedBool }) {
            Icon(
                painter = painter,
                contentDescription = iconDescription
            )
        }
        DropdownMenu(expanded = expandedBool, onDismissRequest = { expandedBool = false }) {
            for (item in menuList) {
                if (clickable){
                    DropdownMenuItem(text = {Text(item.first)}, onClick = { item.second.invoke() })
                }
                else {
                    Text(item.first)
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}
;
@Preview(showBackground = true)
@Composable
fun Preview(){
    MainPage()
}