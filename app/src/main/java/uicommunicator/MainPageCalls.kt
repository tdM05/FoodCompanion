package uicommunicator

import com.example.foodcompanion.screens.MainPage
import com.example.foodcompanion.screens.Notification


/*
Server should send notifications from physician or any updates etc.
 */
fun updateNotifications (message: String) {
    /*Function body shouldn't need to be changed*/
    Notification.message.add(message)
}