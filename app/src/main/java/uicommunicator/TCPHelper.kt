package uicommunicator

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/* Header management */

val appVersion: Long = 20240708122100

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


fun loadHeader
(
    header: String,
    sessionToken: String
): Header
{

    val txTimeString = header.subSequence(0, 14)
    val mcTypeString = header[14]
    val sesTokenString = header.subSequence(15, 47)
    val appVersionString = header.subSequence(47, 61)
    val msgLenString = header.subSequence(61, 67).replace('='.toString().toRegex(), "")

    return Header(
        txTimeString.toString().toLong(),
        mcTypeString == '1',
        sesTokenString.toString(),
        appVersionString.toString().toLong(),
        msgLenString.toLong()
    )

}


data class Transmission(
    var header: Header?,
    var hash: String?,
    var message: String?
)
