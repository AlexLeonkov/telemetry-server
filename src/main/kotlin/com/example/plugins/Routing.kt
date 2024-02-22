package com.example.plugins

import com.example.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean


fun Application.configureRouting() {

    routing {




        post("/request") {
            val telemetryRequest = call.receive<TelemetryRequest>()
            val deviceIdentifier = call.request.header("Device-Identifier")
            val anonymizationType = call.request.header("Anonymization-Type")

            application.log.info("Received headers: ${telemetryRequest.headers.toString()}")


            application.log.info(anonymizationType)
            if (deviceIdentifier != null) {
                try {
                    // Associate the 'deviceIdentifier' with the 'TelemetryRequest'
                    telemetryRequest.deviceIdentifier = deviceIdentifier
                    telemetryRequest.anonymizationType = anonymizationType


                    // Insert the 'TelemetryRequest' into the database
                    DatabaseFactory.insertRequest(telemetryRequest)

                    application.log.info("Request data saved to the database.")
                    call.respond(HttpStatusCode.OK, "Request data saved to the database.")
                } catch (e: Exception) {
                    application.log.error("Error saving request data: ${e.localizedMessage}")
                    call.respond(HttpStatusCode.InternalServerError, "Failed to save request data to the database.")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Device-Identifier header is missing.")
            }
        }

        post("/connection") {
            val telemetryConnection = call.receive<TelemetryConnection>()
            val deviceIdentifier = call.request.header("Device-Identifier")

            if (deviceIdentifier != null) {
                try {
                    // Associate the 'deviceIdentifier' with the 'TelemetryConnection'
                    telemetryConnection.deviceIdentifier = deviceIdentifier

                    // Insert the 'TelemetryConnection' into the database
                    DatabaseFactory.insertConnection(telemetryConnection)

                    application.log.info("Connection data saved to the database.")
                    call.respond(HttpStatusCode.OK, "Connection data saved to the database.")
                } catch (e: Exception) {
                    application.log.error("Error saving connection data: ${e.localizedMessage}")
                    call.respond(HttpStatusCode.InternalServerError, "Failed to save connection data to the database.")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Device-Identifier header is missing.")
            }
        }



        post("/response") {
            val telemetryResponse = call.receive<TelemetryResponse>()
            val deviceIdentifier = call.request.header("Device-Identifier")
            val anonymizationType = call.request.header("Anonymization-Type")

            if (deviceIdentifier != null) {
                try {
                    // Associate the 'deviceIdentifier' with the 'TelemetryResponse'
                    telemetryResponse.deviceIdentifier = deviceIdentifier
                    telemetryResponse.anonymizationType = anonymizationType // Assuming you have a field for this in your data class

                    // Insert the 'TelemetryResponse' into the database
                    DatabaseFactory.insertResponse(telemetryResponse)

                    application.log.info("Response data saved to the database.")
                    call.respond(HttpStatusCode.OK, "Response data saved to the database.")
                } catch (e: Exception) {
                    application.log.error("Error saving response data: ${e.localizedMessage}")
                    call.respond(HttpStatusCode.InternalServerError, "Failed to save response data to the database.")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Device-Identifier header is missing.")
            }
        }



        post("/app-data") {
            val telemetryApp = call.receive<TelemetryApp>()
            try {
                DatabaseFactory.insertApp(telemetryApp)
                application.log.info("App data saved to the database.")
                call.respond(HttpStatusCode.OK, "App data saved to the database.")
            } catch (e: Exception) {
                application.log.error("Error saving app data: ${e.localizedMessage}")
                call.respond(HttpStatusCode.InternalServerError, "Failed to save app data to the database.")
            }
        }


        get("/table-info") {
            try {
                val requestCount = DatabaseFactory.getEntityCount("request")
                val responseCount = DatabaseFactory.getEntityCount("response")
                val appCount = DatabaseFactory.getEntityCount("app")
                val connectionCount = DatabaseFactory.getEntityCount("connection")
                val uniqueRequestDeviceCount = DatabaseFactory.getUniqueDeviceIdentifierCount("request")
                val uniqueResponseDeviceCount = DatabaseFactory.getUniqueDeviceIdentifierCount("response")
                val uniqueConnectionDeviceCount = DatabaseFactory.getUniqueDeviceIdentifierCount("connection")

                val info = TableInfo(
                    requestCount = requestCount,
                    responseCount = responseCount,
                    appCount = appCount,
                    connectionCount = connectionCount,
                    uniqueRequestDeviceCount = uniqueRequestDeviceCount,
                    uniqueResponseDeviceCount = uniqueResponseDeviceCount,
                    uniqueConnectionDeviceCount = uniqueConnectionDeviceCount
                )

                call.respond(info)
            } catch (e: Exception) {
                application.log.error("Error retrieving table information: ${e.localizedMessage}")
                call.respond(HttpStatusCode.InternalServerError, "Failed to retrieve table information.")
            }
        }



        get("/download-database") {
            val databaseFilePath = "/Users/alexleonkov/Desktop/telemetry-server/telemetry.db" // Replace with the actual relative path to your SQLite database file
            val databaseFile = File(databaseFilePath)

            if (databaseFile.exists()) {
                // Prepare the response headers
                val disposition = ContentDisposition.Attachment
                    .withParameter(ContentDisposition.Parameters.FileName, "telemetry.db")
                call.response.header(HttpHeaders.ContentDisposition, disposition.toString())

                // Set the content type for SQLite database files
                call.response.header(HttpHeaders.ContentType, ContentType.Application.OctetStream.toString())

                // Send the database file as a response
                call.respondFile(databaseFile)
            } else {
                call.respond(HttpStatusCode.NotFound, "Database file not found")
            }
        }
        }

}
