package com.example.foodcompanion

import android.util.Log
import java.net.Socket


class Client : Runnable {

    private var connected: Boolean      = false
    private var rsaN: String?           = null
    private var rsaE: String?           = null
    private var sessionToken: String?   = null
    private var appVersion: Long        = 20240708122100

    val isConnected: Boolean get() = connected
    val getAppVersion: Long get() = appVersion

    private fun com() : Boolean {
        val ip = "192.168.56.1"
        val port = 12345
        val client = Socket(ip, port)

        if (client.isConnected)
        {
            Log.d("TCPService", "Connected to $ip:$port")
        }
        else
        {
            Log.e("TCPService", "Could not connect to TCP server.")
            return false
        }

        val oStream = client.outputStream
        val iStream = client.inputStream

        val loginMsg = "NW_CON${getAppVersion}".toByteArray(Charsets.UTF_8)

        // Send the login message
        oStream.write(loginMsg)

        // Get the information.
        val inputBuff = ByteArray(1024)
        val inputBytes = iStream.read(inputBuff)

        if (inputBytes == -1)
        {
            Log.e("TCPService", "Did not receive message from server (0).")
            return false
        }

        try {

            val inputStr = String(inputBuff, 0, inputBytes)
            Log.d("TCPService", "RCV:RAW $inputStr")
            assert(inputStr.length > 5)

            val cd = inputStr.subSequence(0, 6)
            assert(cd == "NW_CON")          // Login successful

            val sesTok = inputStr.subSequence(6, 38)
            val pubKey = inputStr.subSequence(38, inputStr.lastIndex + 1)

            sessionToken = sesTok.toString()
            val delim_index = pubKey.indexOf('!', 0, true)

            rsaN = pubKey.subSequence(0, delim_index).toString()
            rsaE = pubKey.subSequence(delim_index + 1, pubKey.lastIndex + 1).toString()

            Log.i("TCPService", "Received $cd, $sessionToken, $rsaN, $rsaE")

            client.close()
            return true

        }
        catch (e: Exception) {
            Log.e("TCPService", "Did not receive message from server (1): ${e.toString()}.")
            client.close()
            return false
        }

    }

    override fun run() {

        if (!com())
        {
            Log.e("TCPService", "[FATAL] Could not connect to server.")
            // TODO: Error screen for user.
        }
        else
        {
            Log.i("TCPService", "Connected to server successfully.")
        }

    }
}