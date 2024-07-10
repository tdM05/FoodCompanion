package com.example.foodcompanion.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foodcompanion.Food
import com.example.foodcompanion.FoodWidget
import com.example.foodcompanion.R

enum class FoodTypes {
    Breakfast,
    Lunch,
    Dinner,
    None
}

@Composable
fun FoodsPage(
    onBackButtonClicked: (String) -> Unit = {},
) {
    Column {
        Spacer(modifier = Modifier.height(60.dp))
        Row() {
            IconButton(onClick = {onBackButtonClicked("test1234")}) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_back_24dp_5f6368_fill0_wght400_grad0_opsz24),
                    contentDescription = "back arrow"
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            // filters
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()){
                var checkedStarch by remember { mutableStateOf(false) }
                var checkedVegetables by remember { mutableStateOf(false) }
                var checkedFruit by remember { mutableStateOf(false) }
                var checkedDessert by remember { mutableStateOf(false) }
                var checkedBeverages by remember { mutableStateOf(false) }
                var checkedCondiments by remember { mutableStateOf(false) }

                Row (){
                    val modifier = Modifier
                    val colModifier = Modifier.weight(1f)
                    Column (modifier = colModifier){
                        //Starches
                        Text(
                            "Starches",
                            modifier = modifier
                        )
                        Checkbox(
                            checked = checkedStarch,
                            onCheckedChange = {checkedStarch = it},
                            modifier = Modifier.absoluteOffset((-12).dp, 0.dp)
                        )
                        //Vegetables
                        Text(
                            "Vegetables",
                            modifier = modifier
                        )
                        Checkbox(
                            checked = checkedVegetables,
                            onCheckedChange = {checkedVegetables = it},
                            modifier = Modifier.absoluteOffset((-12).dp, 0.dp))
                    }
                    Column(modifier = colModifier){
                        //Fruit
                        Text(
                            "Fruit",
                            modifier = modifier
                        )
                        Checkbox(
                            checked = checkedFruit,
                            onCheckedChange = {checkedFruit = it},
                            modifier = Modifier.absoluteOffset((-12).dp, 0.dp))
                        //dessert
                        Text(
                            "Dessert",
                            modifier = modifier
                        )
                        Checkbox(
                            checked = checkedDessert,
                            onCheckedChange = {checkedDessert = it},
                            modifier = Modifier.absoluteOffset((-12).dp, 0.dp))
                    }
                    Column (modifier = colModifier){

                        //Beverages
                        Text(
                            "Beverages",
                            modifier = modifier
                        )
                        Checkbox(
                            checked = checkedBeverages,
                            onCheckedChange = {checkedBeverages = it},
                            modifier = Modifier.absoluteOffset((-12).dp, 0.dp))
                        //Condiments
                        Text(
                            "Condiments",
                            modifier = modifier
                        )
                        Checkbox(
                            checked = checkedCondiments,
                            onCheckedChange = {checkedCondiments = it},
                            modifier = Modifier.absoluteOffset((-12).dp, 0.dp))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(modifier = Modifier.verticalScroll(rememberScrollState())){
            repeat(10){
                val myFood = Food(foodName = "Brocolli", foodImage = painterResource(id = R.drawable.broccoli_78ec54e),
                    servingSize = "16g",
                    foodCategory = "Vegetables")
                FoodWidget(
                    myFood)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FoodsPreview(){
    FoodsPage()
}