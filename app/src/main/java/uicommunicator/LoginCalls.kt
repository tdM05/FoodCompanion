package uicommunicator

import android.util.Log
import com.example.foodcompanion.Client
import com.example.foodcompanion.TCPInfo
import com.example.foodcompanion.globalTCPInfo
import java.lang.Thread


data class PTInfo(
    val institutionID: String,
    val patientDOB:    Long,
    val patientID:     Long
)

val SC: String = "FC.VIdent"

fun checkPTInfo (
    instID: String,
    ptID:   String,         /* Numeric IDs only! */
    ptDOB:  String         /* Must be in the format YYYYMMDD */
): PTInfo?
{

    return PTInfo("test", 20000101, 0)

}

fun verifyID(
    instID: String,
    ptID:   String,         /* Numeric IDs only! */
    ptDOB:  String,         /* Must be in the format YYYYMMDD */
    pageToNavigateTo: () -> Unit
): Boolean {

    /*
    This function is called after the user enters a valid Institution and school id and presses enter.
    This should communicate with the server to request validation of the user profile.
    If the server validates their request, then this function will call the next main screen.

    Note: open LogCat and put a tag of "LGVerifyID" to view below messages
     */

    Log.d(SC, "Checking patient information.")
    val ptInfo: PTInfo = checkPTInfo(instID, ptID, ptDOB) ?: return false

    Log.d(SC, "Logging In; I(${ptInfo.institutionID}), P(${ptInfo.patientDOB}), D(${ptInfo.patientID})")

    Thread(Client()).start()
    while (globalTCPInfo == null) { Log.v(SC, "Waiting...") }

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

    /* TODO: check information here. */

    // Go to the main page
    pageToNavigateTo()

    return true
}


