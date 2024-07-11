package uicommunicator

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.foodcompanion.appVersion
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/* Header management */

data class Header(
    val H_TX_TIME:      Long,               /* 14 characters */
    val H_MC_TYPE:      Boolean,            /* 1 character */
    val H_SES_TOK:      String,             /* 32 characters */
    val H_APP_VIS:      Long,               /* 14 characters */
    val H_MSG_LEN:      Long                /* 6 characters */
)


@RequiresApi(Build.VERSION_CODES.O)
fun createHeader
            (
    msgLength: Long,
    sessionToken: String
): Pair<String, Header>
{
    val txTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm00"))
    var mcType = "0"
    val header = Header(
        txTime.toLong(),
        false,
        sessionToken,
        appVersion,
        msgLength
    )

    if (header.H_MC_TYPE) { mcType = "1" }

    val appVIS = appVersion.toString()

    var msgLen = msgLength.toString()
    assert(msgLen.length <= 6)
    msgLen = msgLen.padStart(6, '=')

    return Pair("$txTime$mcType$sessionToken$appVIS$msgLen", header)
}

/* Hash */

/* Encryption */

