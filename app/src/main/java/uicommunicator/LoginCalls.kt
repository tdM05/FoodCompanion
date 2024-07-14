package uicommunicator

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.foodcompanion.Client
import com.example.foodcompanion.Food
import com.example.foodcompanion.TCPInfo
import com.example.foodcompanion.globalTCPInfo
import com.example.foodcompanion.NClient
import uicommunicator.Encryptor.RSAEncrypt
import java.security.MessageDigest
import com.example.foodcompanion.NC_reply
import com.example.foodcompanion.NC_comError
import com.example.foodcompanion.NC_replyAvailable
import com.example.foodcompanion.data.FoodCategory
import com.example.foodcompanion.data.Meal
import com.example.foodcompanion.data.parseJson

data class PTInfo(
    val institutionID: String,
    val patientDOB:    Long,
    val patientID:     Long
)

val SC: String = "FC.VIdent"


fun checkPTInfo (
    iID:    String,
    pID:    String,         /* Numeric IDs only! */
    pDOB:   String          /* Must be in the format YYYYMMDD */
): PTInfo?
{

    Log.d(SC, "Checking [$iID], [$pID], [$pDOB]")

    if (
        (iID.trim().length <= 0) ||
        (pID.trim().length <= 0) ||
        (pDOB.trim().length != 8)   /* YYYYMMDD = 8 Chars */
    )
    {
        Log.e(SC, "VID-ERR 1")
        return null
    }

    var pnID: Long? = null
    var pnDOB: Long? = null

    try
    {
        val dobYear: Int = pDOB.subSequence(0, 4).toString().toInt()
        val dobMonth: Int = pDOB.subSequence(4, 6).toString().toInt()
        val dobDate: Int = pDOB.subSequence(6, 8).toString().toInt()

        assert(dobYear > 1900)
        assert(dobMonth in 1..12)
        assert(dobDate  in 1..31)

        pnID = pID.toLong()
        pnDOB= pDOB.toLong()

        assert(pnDOB >= 19000101)
        assert(pnID > 0)
    }
    catch (e: Exception)
    {
        Log.e(SC, "VID-ERR 2 <$e>")
        return null
    }

    return PTInfo(iID.trim(), pnDOB, pnID)
}

@RequiresApi(Build.VERSION_CODES.O)
fun verifyID(
    instID: String,
    ptID:   String,         /* Numeric IDs only! */
    ptDOB:  String,         /* Must be in the format YYYYMMDD */
    enableButton: () -> Unit
): Boolean {

    /*
    This function is called after the user enters a valid Institution and school id and presses enter.
    This should communicate with the server to request validation of the user profile.
    If the server validates their request, then this function will call the next main screen.

    Note: open LogCat and put a tag of "LGVerifyID" to view below messages
     */

    Log.d(SC, "Checking patient information.")
    val ptInfo = checkPTInfo(instID, ptID, ptDOB) // ?: return false

    if (ptInfo == null)
    {
        Log.e(SC, "Invalid login information.")
        //enableButton.invoke()
        return false
    }

    Log.d(SC, "Logging In; I(${ptInfo.institutionID}), P(${ptInfo.patientDOB}), D(${ptInfo.patientID})")

    Thread(Client()).start()
    while (globalTCPInfo == null) { Log.v(SC, "Waiting for TCPClient") }

    val tcpInfo: TCPInfo = globalTCPInfo!!

    if
    (
        !tcpInfo.connected               ||
         tcpInfo.sessionToken == null    ||
         tcpInfo.pubKey == null
    )
    {
        Log.e(SC, "Not connected to server.")
        throw Exception("Not connected to server.")

    }

    Log.i(SC, "Successfully connected to the server.")

    // Encrypt the data using the above key.
    val msgLength = 67 +                                    /* 67 byte header */
            64 +                                            /* 64 byte hash */
            14 +                                            /* DOB (always 14 byte) */
            ptInfo.institutionID.length +                   /* Length of institution ID */
            ptInfo.patientID.toString().length +            /* Length of patient ID */
            2                                               /* 2 delimiter characters */

    /*  TODO: This message will be changed to {instID}~{patientDOB}~{patientID} once encryption is working. */

    val outMessage: String = "${ptInfo.institutionID}~${ptInfo.patientDOB}~${ptInfo.patientID}"
    val header = createHeader(msgLength.toLong(), tcpInfo.sessionToken!!)

    /* TODO:
    *   Encrypt the message.
    *   Run SHA256 on the encrypted message
    *   Send {header}{sha256}{encrypted_message} to the server
    *
    *   Wait for the response and decode it.
    *  */

    //val encryptedMessage: ByteArray = RSAEncrypt(outMessage, tcpInfo.pubKey!!) ?: return false
    var encryptedMessage: ByteArray? = null
    if ( RSAEncrypt(outMessage, tcpInfo.pubKey!!) != null){
        encryptedMessage = RSAEncrypt(outMessage, tcpInfo.pubKey!!)
    }
    else {
        //enableButton.invoke()
        return false
    }
    val hashedMessage : String = MessageDigest.getInstance("SHA-256").digest(encryptedMessage).fold("") { str, it -> str + "%02x".format(it) }

    NClient.hdr = header
    NClient.hmsg = hashedMessage
    NClient.emsg = encryptedMessage

    Thread(NClient()).start()

    while (!NC_replyAvailable) { Log.v(SC, "Waiting for TCPClient2") }

    if (
        NC_comError                 ||
        NC_reply?.hash == null      ||
        NC_reply?.message == null   ||
        NC_reply?.header == null
    ) {
        Log.e(SC, "Login failed.")
        //
        //enableButton.invoke()
        return false
    }

    val dietJson: String = NC_reply?.message!!
    Log.i(SC, "Received diet order: $dietJson")
    val parsedJson = parseJson(dietJson)
    Log.d(SC, "$parsedJson")



    createFoodObjectsFromJson(parsedJson)
    return true
}

