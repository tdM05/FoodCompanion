package com.example.foodcompanion

import android.graphics.Paint.Align
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

@Composable
fun FoodWidget(foodName: String, foodImage: Painter, servingSize: String, foodCategory: String,
               modifier: Modifier = Modifier){
    Box (modifier = Modifier
        .height(80.dp)
        .padding(horizontal = 20.dp)
        .clip(shape = RoundedCornerShape(14.dp))
        .background(color = Color.hsv(0f, 0f, 0.93f))
        ){
        Row(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = foodImage,
                contentDescription = foodName,
                alignment = AbsoluteAlignment.CenterLeft,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column (
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)){
                Text(text = foodName)
                Text(text = "Serving size of: $servingSize")
                Text(text = foodCategory)
            }
            IconButton(
                modifier = Modifier.align(Alignment.CenterVertically).fillMaxSize().wrapContentWidth(align = Alignment.End).padding(8.dp),
                onClick = { /*TODO*/ }
            ) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.delete_24dp_5f6368_fill0_wght400_grad0_opsz24
                    ),
                    contentDescription = "delete button"
                )
            }

        }
    }
    Box(modifier = Modifier.fillMaxSize()){

    }
}

@Preview(showBackground = true)
@Composable
fun Preview(){
    FoodWidget(foodName = "Brocolli", foodImage = painterResource(id = R.drawable.broccoli_78ec54e),
        servingSize = "16g",
        foodCategory = "Vegetables")
}