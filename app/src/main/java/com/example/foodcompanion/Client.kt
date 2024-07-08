package com.example.foodcompanion

import java.io.IOException
import java.io.OutputStream
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import java.util.Random

class Client : Runnable {

    override fun run() {
        val client = Socket("10.0.2.2", 12345)
        client.outputStream.write("Hello from the client!".toByteArray())
        client.close()
    }
}