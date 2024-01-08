package com.example.plugins

import com.example.TelemetryRequest
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean


fun Application.configureRouting() {

    val dataIsTracker = AtomicBoolean(false)


    routing {
        get("/") {
            call.respondText("The value of dataIsTracker is: ${dataIsTracker.get()}")
        }


        // In your Routing.kt
        post("/post") {
            val receivedData = call.receive<TelemetryRequest>()
            // Process the received data as needed
            dataIsTracker.set(receivedData.isTracker)
            application.log.info("Received POST with data: ${receivedData.isTracker}, etc.")
            call.respondText("Received POST with data: ${receivedData.isTracker}, etc.")
        }




        }

}
