package uicommunicator

import com.example.foodcompanion.Food
import com.example.foodcompanion.FoodManager


//This function is called automatically
fun canAddThisFood(food: Food): Pair<Boolean, String> {
    /* Should return whether or not this food can be added for the first part of the pair and
    if the food cannot be added (the first part is false), the second part of the pair should
    have a message as to why the food cannot be added. The second part should be "" if the food can be added
    */
    //TODO
    return Pair(true, "adsfsadfas") //change this!
}



fun getMeal(): List<Food> {
    return FoodManager.myMeal.toList()
}
