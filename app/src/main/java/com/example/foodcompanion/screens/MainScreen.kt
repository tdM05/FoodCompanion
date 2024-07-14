package com.example.foodcompanion.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodcompanion.data.FoodCategory
import com.example.foodcompanion.Food
import com.example.foodcompanion.FoodManager
import com.example.foodcompanion.FoodWidget
import com.example.foodcompanion.R
object Notification {
    var message: MutableList<String> = mutableListOf("test1", "test2asdfasdfasdfsadfasdfasdfasdfasdfasdf")
}

@SuppressLint("InvalidColorHexValue")
@Composable
fun MainPage(
    onFoodButtonClicked: (String) -> Unit = {},
) {
    Column (

    ){
        Spacer(modifier = Modifier.height(40.dp))

        // header
        Row {
            var expandedBool by remember {
                mutableStateOf(false)
            }
            Box {
                IconButton(onClick = { expandedBool = !expandedBool }) {
                    Icon(
                        painter = painterResource(id = R.drawable.account_circle_24dp_5f6368_fill0_wght400_grad0_opsz24),
                        tint = Color(0xFF222222),
                        contentDescription = "Profile information"
                    )
                }

                DropdownMenu(
                    expanded = expandedBool,
                    onDismissRequest = { expandedBool = false },
                    modifier = Modifier.background(color = Color(0xFF1C5D99))
                ) {
                    DropdownMenuItem(
                        text = {Text(text = "Sign Out", color = Color.White)},
                        onClick = {
                            FoodManager.myMeal.clear()
                            expandedBool = false
                            onFoodButtonClicked("Login")
                        }
                    )

                    }
                }
            }
            // profile
//            ExpandableIcon(
//                painter = painterResource(id = R.drawable.account_circle_24dp_5f6368_fill0_wght400_grad0_opsz24),
//                menuList = mutableListOf(
//                    Pair("Profile Information"){
//                        Log.d("debug", "profile information")
//                    },
//                    Pair("Sign Out",
//                        ){
//                        Log.d("debug", "signed out")
//
//                        onFoodButtonClicked("Login")
//                    }
//                ),
//                iconDescription = "Profile"
//            )
            //Spacer(modifier = Modifier.weight(1f))
            // notifications
            var menuList = mutableListOf<Pair<String, () -> Unit>>()
            for (i in 0..Notification.message.size - 1) {
                menuList.add(Pair(Notification.message[i]) {})
            }
//            ExpandableIcon(
//                painter = painterResource(id = R.drawable.notifications_24dp_5f6368_fill0_wght400_grad0_opsz24),
//                menuList = menuList,
//                iconDescription = "Profile",
//                clickable = false
//            )

        Column (
            modifier = Modifier.verticalScroll(rememberScrollState())
        ){
            Text(
                text = "Add Food",
                fontSize = 60.sp,
                modifier = Modifier.padding(16.dp),
                color = Color(0xFF1C5D99)
            )
            val iconModifier = Modifier
                .fillMaxSize()
                .align(Alignment.CenterHorizontally)
            Row (horizontalArrangement = Arrangement.Center){
                val colorForTint = Color(0xFF639FAB)
                Column (
                    modifier = Modifier
                        .size(100.dp)
                        .weight(10f, fill = true)
                        .padding(1.dp)
                ){
                    Text(
                        "Breakfast",
                        color = Color(0xFF1C5D99),
                        modifier = Modifier.align(Alignment.CenterHorizontally))
                    IconButton(
                        onClick = {onFoodButtonClicked(FoodCategory.Breakfast.name)},
                        modifier = iconModifier
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.sunrise),
                            contentDescription = "breakfast",
                            tint = colorForTint,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Column (modifier = Modifier
                    .size(100.dp)
                    .weight(10f, fill = true)
                    .padding(1.dp)
                    .height(50.dp)){
                    Text("Lunch",
                        color = Color(0xFF1C5D99),
                        modifier = Modifier.align(Alignment.CenterHorizontally))
                    IconButton(
                        onClick = {onFoodButtonClicked(FoodCategory.Lunch.name)},
                        modifier = iconModifier
                        ) {
                        Icon(
                            painter = painterResource(id = R.drawable.sun),
                            contentDescription = "breakfast",
                            tint = colorForTint,
                            modifier = Modifier.fillMaxSize(0.9f)
                        )
                    }
                }
                Column (modifier = Modifier
                    .weight(10f, fill = true)
                    .padding(1.dp)
                    .width(20.dp)
                    .height(100.dp)){
                    Text("Dinner",
                        color = Color(0xFF1C5D99),
                        modifier = Modifier.align(Alignment.CenterHorizontally))
                    IconButton(
                        onClick = {onFoodButtonClicked(FoodCategory.Dinner.name)},
                        modifier = iconModifier
                        ) {
                        Icon(
                            painter = painterResource(id = R.drawable.night_mode),
                            contentDescription = "breakfast",
                            tint = colorForTint,
                            modifier = Modifier.fillMaxSize(0.75f)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        //My meal
        val myMealFont = 60.sp
        val statusFont = 14.sp
        val rememberMeal = remember { mutableStateListOf<Food>() }
        rememberMeal.clear()
        for (food in FoodManager.myMeal) {
            rememberMeal.add(food)
        }
        var mealReady by remember {
            mutableStateOf(true)
        }
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()).fillMaxSize()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "My Meal",
                    fontSize = myMealFont,
                    modifier = Modifier.padding(16.dp),
                    color = Color(0xFF1C5D99))
                var infoRemember by remember {
                    mutableStateOf(false)
                }
                IconButton(
                    onClick = { infoRemember = !infoRemember },
                    Modifier
                ) {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.help_24dp_5f6368_fill0_wght400_grad0_opsz24
                        ),
                        contentDescription = null
                    )
                    DropdownMenu(
                        expanded = infoRemember,
                        onDismissRequest = { infoRemember = false },
                        modifier = Modifier.background(color = Color(0xFF222222))
                    ) {
                        var calories = 0f
                        var starches = 0f
                        var fiber = 0f
                        var sugar = 0f
                        var protein = 0f
                        var trans = 0f
                        var saturated = 0f
                        for (food in rememberMeal) {
                            calories += getNumFromString(food.foodDetails[0])
                            starches += getNumFromString(food.foodDetails[1])
                            fiber += getNumFromString(food.foodDetails[2])
                            sugar += getNumFromString(food.foodDetails[3])
                            protein += getNumFromString(food.foodDetails[4])
                            trans += getNumFromString(food.foodDetails[5])
                            saturated += getNumFromString(food.foodDetails[6])
                        }
                        val textcol = Color.White
                        Text("Total:", fontWeight = FontWeight.Bold, color = textcol, modifier = Modifier.padding(8.dp))
                        Text("Calories: $calories", color = textcol, modifier = Modifier.padding(8.dp))
                        Text("Starches: $starches", color = textcol, modifier = Modifier.padding(8.dp))
                        Text("Fibers: $fiber", color = textcol, modifier = Modifier.padding(8.dp))
                        Text("Sugars: $sugar", color = textcol, modifier = Modifier.padding(8.dp))
                        Text("Proteins: $protein", color = textcol, modifier = Modifier.padding(8.dp))
                        Text("Trans Fat: $trans", color = textcol, modifier = Modifier.padding(8.dp))
                        Text("Saturated Fat: $saturated", color = textcol, modifier = Modifier.padding(8.dp))
                        //food details list is this format
                        //carbs, proteins, fats (el will input only the numbers in this order)
                    }
                }

            }
            if (!rememberMeal.isEmpty()) {
                for (food in rememberMeal) {
                    FoodWidget(
                        food = food,
                        removeFromFoodList = { rememberMeal.remove(food) },
                        updateMealReady = {status: Boolean -> mealReady = status}
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Log.d("debug", "removed food")
                }
            } else {
                Text(
                    text = "Nothing Here", fontSize = 16.sp, modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(80.dp),
                    color = Color(0xFF639FAB)
                )
            }
        }
    }
}


fun getNumFromString(str: String): Float {
    var equalIndex: Int = str.length - 1
    for (i in 0..(str.length-1)){
        if (str[i] == '=') {
            equalIndex = i
            break
        }
    }

    return str.slice((equalIndex+1)..<str.length).toFloat()
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
                tint = Color(0xFF222222),
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