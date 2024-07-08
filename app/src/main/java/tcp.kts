import java.io.ObjectInputStream
import java.net.ServerSocket

fun tcp() {
    val serverSocket = ServerSocket(12345) // Replace 12345 with your desired port number
    println("Server running on port ${serverSocket.localPort}")

    while (true) {
        val clientSocket = serverSocket.accept()
        println("Client connected: ${clientSocket.inetAddress.hostAddress}")

        val inputStream = clientSocket.getInputStream()
        val objectInputStream = ObjectInputStream(inputStream)

        try {
            val receivedData = objectInputStream.readObject()
            if (receivedData is Map<*, *>) {
                val receivedDictionary = receivedData as Map<String, Any>
                println("Received dictionary from client: $receivedDictionary")
                // Process the received dictionary here
            } else {
                println("Received data is not a dictionary")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            objectInputStream.close()
            clientSocket.close()
        }
    }
}