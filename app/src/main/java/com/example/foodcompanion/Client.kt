package com.example.foodcompanion

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import java.net.Socket


val appVersion: Long        = 20240708122100

/* Network */

data class TCPInfo
(
    var connected: Boolean              = false,
    var pubKey: String?                 = null,
    var sessionToken: String?           = null,
    var appVersion: Long                = 20240708122100
)

var globalTCPInfo: TCPInfo?             = null

class Client : Runnable
{

    private val SC: String              = "TCPClient"

    private var connected: Boolean      = false
    private var pubKey: String?         = null
    private var sessionToken: String?   = null
    private val appVersion: Long        = 20240708122100

    private val isConnected: Boolean get() = connected
    private val getAppVersion: Long get() = appVersion

    val tcpInfo: TCPInfo get() = TCPInfo(connected, pubKey, sessionToken, appVersion)

    private fun connectToServer() : Boolean {

        /*
        * TODO:
        *   Make the app load the local IP address of the device automatically.
        * */
        val ip   = "192.168.56.1"
        val port = 12345

        Log.d(SC, "Trying to connect to $ip:$port.")

        val client = Socket(ip, port)

        if (client.isConnected)
            Log.d(SC, "Connected to TCP server.")

        else
        {
            Log.e(SC, "Could not connect to TCP server.")
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
            Log.e(SC, "Did not receive message from server (0).")
            return false
        }

        try {

            val inputStr = String(inputBuff, 0, inputBytes)
            Log.d(SC, "RCV:RAW $inputStr")
            assert(inputStr.length > 5)

            val cd = inputStr.subSequence(0, 6)
            assert(cd == "NW_CON")          // Login successful

            sessionToken = inputStr.subSequence(6, 38).toString()
            pubKey = inputStr.subSequence(38, inputStr.lastIndex + 1).toString()

            Log.i(SC, "Received $cd, $sessionToken, $pubKey")

            client.close()
            return true
        }
        catch (e: Exception)
        {
            Log.e("TCPService", "Did not receive message from server (1): ${e.toString()}.")

            client.close()
            return false
        }

    }

    override fun run() {

        if (!connectToServer())
        {
            Log.e("TCPService", "[FATAL] Could not connect to server.")
            connected = false
            // TODO: Error screen for user.
        }
        else
        {
            Log.i("TCPService", "Connected to server successfully.")
            connected = true
        }

        globalTCPInfo = tcpInfo

    }

}


class NClient: Runnable
{
    val SC = "TCPClient2"

    companion object
    {
        var hdr: Pair<String, Any>? = null
        var hmsg: String? = null
        var emsg: ByteArray? = null
    }

    override fun run()
    {
        val hb: ByteArray = (hdr ?: return).first.encodeToByteArray()
        val hm: ByteArray = (hmsg ?: return).encodeToByteArray()
        val em: ByteArray = emsg ?: return


        val s = Socket("192.168.56.1", 12345)

        var out = hb
        out += hm
        out += em
//        out += em
//        out += hb
//        out += hm

        s.outputStream.write(out)

    }
}
