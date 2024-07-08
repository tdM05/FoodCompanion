package com.example.yourapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.io.ObjectInputStream
import java.net.ServerSocket

class ServerService : Service() {

    private lateinit var serverThread: Thread

    override fun onCreate() {
        super.onCreate()
        serverThread = Thread(ServerRunnable())
        serverThread.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        serverThread.interrupt()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private inner class ServerRunnable : Runnable {
        override fun run() {
            try {
                val serverSocket = ServerSocket(12345)
                Log.d("ServerService", "Server running on port ${serverSocket.localPort}")

                while (!Thread.currentThread().isInterrupted) {
                    val clientSocket = serverSocket.accept()
                    Log.d("ServerService", "Client connected: ${clientSocket.inetAddress.hostAddress}")

                    val inputStream = clientSocket.getInputStream()
                    val objectInputStream = ObjectInputStream(inputStream)

                    try {
                        val receivedData = objectInputStream.readObject()
                        if (receivedData is Map<*, *>) {
                            val receivedDictionary = receivedData as Map<String, Any>
                            Log.d("ServerService", "Received dictionary from client: $receivedDictionary")
                            // Process the received dictionary here
                        } else {
                            Log.d("ServerService", "Received data is not a dictionary")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        objectInputStream.close()
                        clientSocket.close()
                    }
                }
                serverSocket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
