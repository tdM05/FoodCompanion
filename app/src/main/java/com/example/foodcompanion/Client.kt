package com.example.foodcompanion

import android.util.Log
import uicommunicator.Transmission
import uicommunicator.appVersion
import uicommunicator.loadHeader
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.Socket
import java.util.Collections
import java.util.Locale
import java.lang.StringIndexOutOfBoundsException


val TCP_ERR_GENERAL             = "ERR.EXIT"
val TCP_ERR_BAD_HEADER          = "ERR.HEDR"
val TCP_ERR_BAD_REQUEST         = "ERR.RQST"
val TCP_ERR_PATIENT_NOT_FOUND   = "ERR.PTNF"
val TCP_ERR_BAD_TRANSMISSION    = "ERR.TRNS"
val TCP_ERR_CLIENT_VERSION      = "ERR.CAVS"
val TCP_ERR_INCOMPLETE_MESSAGE  = "ERR.INCM"

val TCP_DEFAULT_RECV_LEN        = 1024

/* Network */

fun getIPAddress(): MutableList<String> {
    val ipv4Addrs = mutableListOf("")

    try
    {
        val interfaces: List<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces())
        for (intf in interfaces)
        {
            val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
            for (addr in addrs)
            {
                if (!addr.isLoopbackAddress)
                {
                    val sAddr: String = addr.hostAddress ?: continue

                    if (sAddr.indexOf(':') < 0)
                        ipv4Addrs.add(sAddr)
                }
            }
        }
    } catch (ignored: Exception) {}

    return ipv4Addrs
}

val ips: MutableList<String> = getIPAddress()
var sip: String? = null
val port = 12345

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

    private val isConnected: Boolean get()  = connected
    private val getAppVersion: Long get()   = appVersion

    val tcpInfo: TCPInfo get() = TCPInfo(connected, pubKey, sessionToken, appVersion)

    private fun connectToServer() : Boolean {

        /*
        * TODO:
        *   Make the app load the local IP address of the device automatically.
        * */

        var client: Socket? = null
        ips.add("10.0.2.2")
        Log.d(SC, "$ips")

        for (ip in ips.iterator())
        {
            if (ip == "") continue

            Log.d(SC, "Trying to connect to $ip:$port.")

            try
            {
                client = Socket(ip, port)
                sip = ip
            }
            catch (ignore: Exception)
            {
                continue
            }

        }

        client ?: return false

        if (sip == null)
        {
            Log.e(SC, "Could not connect to any IP")
            client.close()
            return false
        }

        if (client.isConnected)
            Log.d(SC, "Connected to TCP server.")
        else
        {
            Log.e(SC, "Could not connect to TCP server.")
            client.close()
            return false
        }

        val oStream = client.outputStream
        val iStream = client.inputStream

        val loginMsg = "NW_CON${getAppVersion}".toByteArray(Charsets.UTF_8)

        // Send the login message
        oStream.write(loginMsg)

        // Get the information.
        val inputBuff = ByteArray(TCP_DEFAULT_RECV_LEN)
        val inputBytes = iStream.read(inputBuff)

        if (inputBytes == -1)
        {
            Log.e(SC, "Did not receive message from server (0).")
            client.close()
            return false
        }

        try {

            val inputStr = String(inputBuff, 0, inputBytes)
            Log.d(SC, "RCV:RAW $inputStr")

            if (
                inputStr == TCP_ERR_GENERAL             ||
                inputStr == TCP_ERR_CLIENT_VERSION      ||
                inputStr == TCP_ERR_BAD_HEADER          ||
                inputStr == TCP_ERR_BAD_REQUEST         ||
                inputStr == TCP_ERR_BAD_TRANSMISSION    ||
                inputStr == TCP_ERR_INCOMPLETE_MESSAGE  ||
                inputStr == TCP_ERR_PATIENT_NOT_FOUND   ||
                inputStr.contains("ERR", ignoreCase = false)
            )
            {
                Log.d(SC, "ERROR: $inputStr")
                client.close()
                return false
            }

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


var NC_replyAvailable: Boolean = false
var NC_comError: Boolean       = false
var NC_reply: Transmission?    = null


class NClient: Runnable
{
    private val SC = "TCPClient2"

    companion object
    {
        var hdr: Pair<String, Any>? = null
        var hmsg: String? = null
        var emsg: ByteArray? = null
    }

    override fun run()
    {

        NC_replyAvailable = false
        NC_comError = false
        NC_reply = null

        sip = null

        if (globalTCPInfo == null)
        {
            Log.e(SC, "Cannot send message - session not established.")
            return
        }

        if (globalTCPInfo!!.sessionToken == null)
        {
            Log.e(SC, "Cannot send message - session not established.")
            return
        }

        val hb: ByteArray = (hdr ?: return).first.encodeToByteArray()
        val hm: ByteArray = (hmsg ?: return).encodeToByteArray()
        val em: ByteArray = emsg ?: return

        var s: Socket? = null

        for (ip in ips.iterator())
        {
            if (ip == "") continue

            Log.d(SC, "Trying to connect to $ip:$port.")

            try
            {
                s = Socket(ip, port)
                sip = ip
            }
            catch (ignore: Exception)
            {
                continue
            }

        }

        s ?: return

        if (sip == null)
        {
            Log.e(SC, "Could not connect to any IP")
            s.close()
            return
        }

        var out = hb
        out += hm
        out += em

        Log.i(SC, "Sending HDrHM-format message to server.")
        s.outputStream.write(out)

        var inputBuff = ByteArray(TCP_DEFAULT_RECV_LEN)
        var inputBytes = s.inputStream.read(inputBuff)

        val inputStr = String(inputBuff, 0, inputBytes)
        Log.d(SC, "RECV: $inputStr")

        if (inputStr == "")
        {
            Log.w(SC, "Connection closed.")

            NC_replyAvailable = true
            NC_comError = true
            NC_reply = Transmission(null, null, null)

            s.close()

            return
        }

//      Compare to error codes.
        if (
            inputStr == TCP_ERR_GENERAL             ||
            inputStr == TCP_ERR_CLIENT_VERSION      ||
            inputStr == TCP_ERR_BAD_HEADER          ||
            inputStr == TCP_ERR_BAD_REQUEST         ||
            inputStr == TCP_ERR_BAD_TRANSMISSION    ||
            inputStr == TCP_ERR_INCOMPLETE_MESSAGE  ||
            inputStr == TCP_ERR_PATIENT_NOT_FOUND   ||
            inputStr.contains("ERR", ignoreCase = false)
        )
        {
            Log.w(SC, "SC_WARN: $inputStr")

            NC_replyAvailable = true
            NC_comError = true
            NC_reply = Transmission(null, null, inputStr)

            s.close()

            return
        }

        Log.d(SC, "Decoding meal options...")

// We got a message; (1) parse header, (2) load all of the message (if not already)
        val header = loadHeader(
            inputStr.subSequence(0, 67).toString(),
            globalTCPInfo?.sessionToken!!
        )

        if ((header.H_MSG_LEN + 64 + 67) > TCP_DEFAULT_RECV_LEN)
        {
            // Need to receive more bytes
            val toReceive = (header.H_MSG_LEN + 64 + 67) - TCP_DEFAULT_RECV_LEN

            val pBuff = ByteArray(toReceive.toInt())
            val pBytes = s.inputStream.read(pBuff)

            inputBuff += pBuff
            inputBytes += pBytes
        }

        val finalReplyStr = String(inputBuff, 0, inputBytes)

        s.close()

        try
        {
            val hashStr = finalReplyStr.subSequence(67, 131).toString()
            val msgStr = finalReplyStr.subSequence(131, finalReplyStr.lastIndex + 1).toString()

            NC_reply = Transmission(header, hashStr, msgStr)
            NC_comError = false
            NC_replyAvailable = true

        }
        catch (exc: StringIndexOutOfBoundsException)
        {
            NC_reply = Transmission(null, null, TCP_ERR_GENERAL)
            NC_comError = true
            NC_replyAvailable = true

            return
        }
    }
}
