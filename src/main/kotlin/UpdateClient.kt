//import androidx.compose.ui.window.application
////класс-сервер для обновлений
//fun main() = application {
//    //todo корутина для подключения к серверу
//}

import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

fun main() = runBlocking {
    // Connect to the server (adjust host/port if needed)
    val socket = withContext(Dispatchers.IO) { Socket("localhost", 8080) }
    println("Connected to echo server")

    try {
        // Wrap socket streams with readers/writers
        val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
        val writer = PrintWriter(socket.getOutputStream(), true)

        // Example messages to send
        val messages = listOf("Hello", "Kotlin coroutines", "exit") // "exit" won't be echoed

        for (msg in messages) {
            println("Sending: $msg")
            writer.println(msg)

            // Read the echo response (suspends until data arrives)
            val response = withContext(Dispatchers.IO) { reader.readLine() }
            if (response != null) {
                println("Received: $response")
            } else {
                println("Server closed connection")
                break
            }

            // Small delay between messages (just for demonstration)
            delay(500)
        }
    } catch (e: Exception) {
        println("Error: ${e.message}")
    } finally {
        socket.close()
        println("Disconnected")
    }
}