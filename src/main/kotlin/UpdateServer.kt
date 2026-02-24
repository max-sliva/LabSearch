import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

//класс-сервер для обновлений
//todo корутина для подключения клиентов к серверному сокету
fun main() = runBlocking {
    val serverSocket = ServerSocket(8080)
    println("Echo server running on port 8080")

    // Keep accepting connections
    while (true) {
        // Suspend until a client connects – the accept call is moved to IO dispatcher
        val socket = withContext(Dispatchers.IO) { serverSocket.accept() }
        println("Client connected: ${socket.inetAddress.hostAddress}")

        // Launch a new coroutine to handle this client
        launch {
            handleClient(socket)
        }
    }
}

suspend fun handleClient(socket: Socket) {
    // Use IO dispatcher for all blocking operations with this client
    withContext(Dispatchers.IO) {
        try {
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val writer = PrintWriter(socket.getOutputStream(), true)

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                println("Received: $line")
                writer.println("Echo from server: $line")
            }
        } catch (e: Exception) {
            println("Error handling client: ${e.message}")
        } finally {
            socket.close()
        }
    }
}