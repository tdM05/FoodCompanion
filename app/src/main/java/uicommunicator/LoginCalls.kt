package uicommunicator
import android.util.Log

fun verifyID(patientID: String, institutionID: String, birthday: String) {
    /*
    This function is called after the user enters a valid Institution and school id and presses enter.
    This should communicate with the server to request validation of the user profile.
    If the server validates their request, then this function will call the next main screen.

    Note: open LogCat and put a tag of "debug" to view below messages
     */

    Log.d(
        "debug", "patientID: " + patientID + ", " + "institutionID: " +
                institutionID + ", birthday: " + birthday
    )

    var accepted = true  // the server should verify this!

    if (accepted) {

    }
}


