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


@Serializable
data class TableInfo(
    val requestCount: Int,
    val responseCount: Int,
    val appCount: Int,
    val uniqueRequestDeviceCount: Int,
    val uniqueResponseDeviceCount: Int
)


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
    val isTracker: Boolean,
    var deviceIdentifier: String? = null
)

    @Serializable
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
        val isTracker: Boolean = false,
        var deviceIdentifier: String? = null
    )






@Serializable
data class TelemetryApp(
    val packageName: String,
    val label: String,
    val versionName: String,
    val versionCode: Long,
    val isInstalled: Boolean,
    val isSystem: Boolean,
    val flags: Int,
    // Ensure all fields are serializable or annotated with @Transient if not needed for serialization
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
