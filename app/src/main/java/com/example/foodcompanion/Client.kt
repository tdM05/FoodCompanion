package com.example.foodcompanion

import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException

class Client : Runnable {

    override fun run() {
        try {
            // Specify the IP and the port (needs to be the same as the server)
            val client = Socket("10.0.2.2", 12345)

            // Send data to server
            val outputStream: OutputStream = client.outputStream
            // Change the message to send to server
            val message = "Hello from the client!"
            outputStream.write(message.toByteArray())

            // Receive response from server
            val inputStream: InputStream = client.inputStream
            val buffer = ByteArray(1024)
            val bytesRead = inputStream.read(buffer)
            // If it is a valid message then print it
            if (bytesRead != -1) {
                val response = String(buffer, 0, bytesRead)
                Log.d("ClientService","Received response from server: $response")
                // Handle the response as needed
            }

            client.close()
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}