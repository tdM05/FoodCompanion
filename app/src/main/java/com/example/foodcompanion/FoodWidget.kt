package com.example.foodcompanion

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class Food (
    val foodName: String,
    val foodImage: Painter,
    val servingSize: String,
    val foodCategory: String){
}

//to add use global variable and when you go back , it will recompose
//to delete, pass in a delete function to the widget so this function mutates the remember var.
object FoodManager {
    val myMeal: MutableList<Food> = mutableListOf<Food>()
    var filterStarches: Boolean = false
    var filterVegetables: Boolean = false
    var filterFruits: Boolean = false
    var filterdessert: Boolean = false
    var filterbeverages: Boolean = false
    var filterCondiments: Boolean = false
}

@Composable
fun FoodWidget(
    food: Food,
    removeFromFoodList: (() -> Unit)? = null,
    modifier: Modifier = Modifier){
    Box (modifier = Modifier
        .height(80.dp)
        .padding(horizontal = 20.dp)
        .clip(shape = RoundedCornerShape(14.dp))
        .background(color = Color.hsv(0f, 0f, 0.93f))
        ){
        Row(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = food.foodImage,
                contentDescription = food.foodName,
                alignment = AbsoluteAlignment.CenterLeft,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column (
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)){
                Text(text = food.foodName)
                Text(text = "Serving size of:" + food.servingSize)
                Text(text = food.foodCategory)
            }
            if (removeFromFoodList != null) {
                IconButton(
                    modifier = Modifier.align(Alignment.CenterVertically).fillMaxSize()
                        .wrapContentWidth(align = Alignment.End).padding(8.dp),
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
                IconButton(
                    modifier = Modifier.align(Alignment.CenterVertically).fillMaxSize()
                        .wrapContentWidth(align = Alignment.End).padding(8.dp),
                    onClick = {
                        FoodManager.myMeal.add(food)
                        Log.d("debug", "myMeal has "+FoodManager.myMeal.size.toString()+ " items")

                    }
                ) {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.add_circle_24dp_e8eaed_fill0_wght400_grad0_opsz24
                        ),
                        contentDescription = "add button"
                    )
                }
            }

        }
    }
    Box(modifier = Modifier.fillMaxSize()){

    }
}

@Preview(showBackground = true)
@Composable
fun Preview(){
    val myFood = Food(foodName = "Brocolli", foodImage = painterResource(id = R.drawable.broccoli_78ec54e),
        servingSize = "16g",
        foodCategory = "Vegetables")
    FoodWidget(food = myFood)
}