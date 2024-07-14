package com.example.foodcompanion

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uicommunicator.canAddThisFood

class Food (
    val foodName: String,
    val foodImageID: Int,
    val servingSize: String,
    val foodCategory: String,
    val foodDetails: List<String> = emptyList()
){
}

//to add use global variable and when you go back , it will recompose
//to delete, pass in a delete function to the widget so this function mutates the remember var.
object FoodManager {
    val myMeal: MutableList<Food> = mutableListOf<Food>()
    // the second part (the string) refers to breakfast, lunch, and dinner
    val foodOptions: MutableList<Pair<Food, String>> = mutableListOf()
    var filterStarches: Boolean = false
    var filterVegetables: Boolean = false
    var filterFruits: Boolean = false
    var filterDessert: Boolean = false
    var filterBeverages: Boolean = false
    var filterCondiments: Boolean = false
}

@Composable
fun FoodWidget(
    food: Food,
    removeFromFoodList: (() -> Unit)? = null,
    updateMealReady: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier){
    var detailsEnabled by remember {
        mutableStateOf(false)
    }
    Box (modifier = Modifier
        .height(80.dp)
        .padding(horizontal = 20.dp)
        .clip(shape = RoundedCornerShape(14.dp))
        .background(color = Color(0xFF1C5D99))
        .clickable { detailsEnabled = !detailsEnabled }
        ){
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.height(80.dp).width(80.dp).
                background(color = Color(0xFFBBCDE5))
            ) {
                Icon(
                    painter = painterResource(id = food.foodImageID),
                    contentDescription = food.foodName,
                    tint = Color(0xFF639FAB),
                    modifier = Modifier.size(60.dp).align(Alignment.Center)
                    //alignment = AbsoluteAlignment.CenterLeft,
                    //modifier = Modifier.aspectRatio(1f)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column (
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)){
                val col = Color(0xFFFFFFFF)
                Text(text = food.foodName, color = col)
                Text(text = "Serving size of:" + food.servingSize, color = col)
                Text(text = food.foodCategory, color = col)
            }
            if (removeFromFoodList != null) {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .fillMaxSize()
                        .wrapContentWidth(align = Alignment.End)
                        .padding(8.dp),
                    onClick = {
                        FoodManager.myMeal.remove(food)
                        removeFromFoodList.invoke()
                    }
                ) {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.delete_24dp_5f6368_fill0_wght400_grad0_opsz24
                        ),
                        contentDescription = "delete button"
                    )
                }
            }
            else{
                val context = LocalContext.current
                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .fillMaxSize()
                        .wrapContentWidth(align = Alignment.End)
                        .padding(8.dp),
                    onClick = {
                        val canAddThisFood = canAddThisFood(food)
                        if (canAddThisFood.first) {
                            FoodManager.myMeal.add(food)
                            Log.d("debug", "myMeal has "+FoodManager.myMeal.size.toString()+ " items")
                        }
                        else {
                            Toast.makeText(
                                context,
                                canAddThisFood.second,
                                Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.add_circle_24dp_e8eaed_fill0_wght400_grad0_opsz24
                        ),
                        contentDescription = "add button",
                        tint = Color(0xFFFFFFFF)
                    )
                }
            }

        }
        DropdownMenu(expanded = detailsEnabled,
            onDismissRequest = { detailsEnabled = false},

            modifier = Modifier.background(color = Color(0xFF1C5D99))
        ) {
            for (detail in food.foodDetails) {
                Text(detail, color = Color(0xFFFFFFFF),
                    modifier = Modifier.padding(8.dp))
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()){

    }
}

@Preview(showBackground = true)
@Composable
fun Preview(){
    val myFood = Food(foodName = "Ice cream", foodImageID = R.drawable.desserticon,
        servingSize = "16g",
        foodCategory = "Vegetables")
    FoodWidget(food = myFood)
}