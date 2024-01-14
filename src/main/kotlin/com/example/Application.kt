package com.example

import com.example.plugins.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable



// Your TelemetryRequest data class
@Serializable
data class TelemetryRequest(
    val id: Int,
    val timestamp: Long,
    val reqResId: Int,
    val headers: String,
    val content: String,
    val contentLength: Int,
    val method: String,
    val remoteHost: String,
    val remotePath: String,
    val remoteIp: String,
    val remotePort: Int,
    val localIp: String,
    val localPort: Int,
    val initiatorId: Int,
    val initiatorPkg: String,
    val isTracker: Boolean
)

data class TelemetryResponse(
    val id: Int,
    val timestamp: Long,
    val reqResId: Int,
    val headers: String,
    val content: String,
    val contentLength: Int,
    val statusCode: Int,
    val statusMsg: String,
    val remoteHost: String,
    val remoteIp: String,
    val remotePort: Int,
    val localIp: String,
    val localPort: Int,
    val initiatorId: Int,
    val initiatorPkg: String,
    val isTracker: Boolean = false
)






data class TelemetryApp(
    val id: Int,
    val timestamp: Long,
    val PrimaryKey: String,
    val ColumnInfo: String,
    val packageName: String,
    val label: String,
    val versionName: String,
    val versionCode: Long,
    val isInstalled: Boolean = true,
    val isSystem: Boolean = false,
    val flags: Int = 0,
    // Additional telemetry fields can be added here
    // For example:
    val usageDuration: Long,
    val foregroundTime: Long,
    val dataUsage: Long
)

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {


    DatabaseFactory.init()


    // Install JSON Content Negotiation
    install(ContentNegotiation) {
        json()
    }

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        anyHost() // Allow requests from any host
        allowCredentials = true
    }


    // Configure your routing
    configureRouting()
}
