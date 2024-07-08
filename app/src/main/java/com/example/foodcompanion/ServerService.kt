package com.example.foodcompanion
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.io.ObjectInputStream
import java.net.InetAddress
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
                val serverPort = 12345
                val serverIpAddress = "10.0.2.1"
                val serverSocket = ServerSocket(serverPort, 0, InetAddress.getByName(serverIpAddress))
                Log.d("ServerService", "Server running on IP $serverIpAddress and port $serverPort")


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
                Log.d("ServerService","Failed to start server")
            }
        }
    }
}
