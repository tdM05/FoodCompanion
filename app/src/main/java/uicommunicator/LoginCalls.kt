package uicommunicator
import android.util.Log
import com.example.foodcompanion.UserInformation

fun verifyID(
    patientID: String,
    institutionID: String,
    birthday: String,
    pageToNavigateTo: () -> Unit) {
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

    /*TODO
    Verify accepted here
    * */

    if (accepted) {
        UserInformation.birthday = birthday
        UserInformation.patientID = patientID
        UserInformation.institutionID = institutionID
        // this goes to main page
        pageToNavigateTo()
    }
}


