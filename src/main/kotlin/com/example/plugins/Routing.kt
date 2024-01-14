package com.example.plugins

import com.example.TelemetryRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
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
            try {
                // Save the received data to the database
                DatabaseFactory.insertTelemetry(receivedData)

                application.log.info("Saved telemetry data to the database: $receivedData")
                call.respondText("Telemetry data saved to the database.")
            } catch (e: Exception) {
                // Log the exception and respond with an error message
                application.log.error("Error saving telemetry data: ${e.localizedMessage}")
                call.respondText("Failed to save telemetry data to the database.", status = HttpStatusCode.InternalServerError)
            }
        }




        }

}
