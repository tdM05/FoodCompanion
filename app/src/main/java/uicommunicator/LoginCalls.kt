package uicommunicator
import android.util.Log

fun verifyID(patientID: String, institutionID: String = "") {
    /*
    This function is called after the user enters a valid Institution and school id and presses enter

    Note: open LogCat and put a tag of "debug" to view below messages
     */

    Log.d(
        "debug", "patientID: " + patientID + ", " + "institutionID: " +
                institutionID
    )
}


