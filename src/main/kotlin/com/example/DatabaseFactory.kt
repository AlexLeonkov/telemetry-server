// DatabaseFactory.kt

import com.example.TelemetryApp
import com.example.TelemetryRequest
import com.example.TelemetryResponse
import java.sql.Connection
import java.sql.DriverManager
import kotlin.reflect.jvm.internal.impl.load.java.JavaClassFinder.Request

object DatabaseFactory {
    // Step 1: Define the JDBC URL for the SQLite database.
    private const val JDBC_URL = "jdbc:sqlite:telemetry.db"

    // Step 2: Initialize the database by creating tables.
    fun init() {
        // Explicitly load the SQLite JDBC driver (this is often not needed, but doesn't hurt to have)
        Class.forName("org.sqlite.JDBC")

        // Create the telemetry table if it doesn't exist
//        createTelemetryTable()
        createAppTable()
        createRequestTable()
        createResponseTable()
    }

    // Step 3: Create the telemetry table in the database if it doesn't exist.


    private fun createRequestTable() {
        DriverManager.getConnection(JDBC_URL).use { conn ->
            val sql = """
            CREATE TABLE IF NOT EXISTS request (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                timestamp INTEGER,
                reqResId INTEGER,
                headers TEXT,
                content TEXT,
                contentLength INTEGER,
                method TEXT,
                remoteHost TEXT,
                remotePath TEXT,
                remoteIp TEXT,
                remotePort INTEGER,
                localIp TEXT,
                localPort INTEGER,
                initiatorId INTEGER,
                initiatorPkg TEXT,
                isTracker BOOLEAN,
                deviceIdentifier TEXT
            );
        """.trimIndent()
            conn.createStatement().execute(sql)
        }
    }


    private fun createResponseTable() {
        DriverManager.getConnection(JDBC_URL).use { conn ->
            val sql = """
            CREATE TABLE IF NOT EXISTS response (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                timestamp INTEGER,
                reqResId INTEGER,
                headers TEXT,
                content TEXT,
                contentLength INTEGER,
                statusCode INTEGER,
                statusMsg TEXT,
                remoteHost TEXT,
                remoteIp TEXT,
                remotePort INTEGER,
                localIp TEXT,
                localPort INTEGER,
                initiatorId INTEGER,
                initiatorPkg TEXT,
                isTracker BOOLEAN,
               deviceIdentifier TEXT
            );
        """.trimIndent()
            conn.createStatement().execute(sql)
        }
    }


    private fun createAppTable() {
        DriverManager.getConnection(JDBC_URL).use { conn ->
            val sql = """
            CREATE TABLE IF NOT EXISTS app (
                packageName TEXT PRIMARY KEY,
                label TEXT,
                versionName TEXT,
                versionCode INTEGER,
                isInstalled BOOLEAN,
                isSystem BOOLEAN,
                flags INTEGER
            );
        """.trimIndent()
            conn.createStatement().execute(sql)
        }
    }


    // Modify the insertTelemetry method to handle a TelemetryRequest object.

    fun insertRequest(request: TelemetryRequest) {
        DriverManager.getConnection(JDBC_URL).use { conn ->
            val sql = """
                INSERT INTO request (
                    timestamp, reqResId, headers, content, contentLength, method,
                    remoteHost, remotePath, remoteIp, remotePort, localIp, localPort,
                    initiatorId, initiatorPkg, isTracker, deviceIdentifier
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent()
            conn.prepareStatement(sql).use { pstmt ->
                pstmt.setLong(1, request.timestamp)
                pstmt.setInt(2, request.reqResId)
                pstmt.setString(3, request.headers)
                pstmt.setString(4, request.content)
                pstmt.setInt(5, request.contentLength)
                pstmt.setString(6, request.method)
                pstmt.setString(7, request.remoteHost)
                pstmt.setString(8, request.remotePath)
                pstmt.setString(9, request.remoteIp)
                pstmt.setInt(10, request.remotePort)
                pstmt.setString(11, request.localIp)
                pstmt.setInt(12, request.localPort)
                pstmt.setInt(13, request.initiatorId)
                pstmt.setString(14, request.initiatorPkg)
                pstmt.setBoolean(15, request.isTracker)
                pstmt.setString(16, request.deviceIdentifier) // Use deviceIdentifier from header
                pstmt.executeUpdate()
            }
        }
    }

    // Modify the insertResponse method to handle a TelemetryResponse object and deviceIdentifier from header.
    fun insertResponse(response: TelemetryResponse) {
        DriverManager.getConnection(JDBC_URL).use { conn ->
            val sql = """
                INSERT INTO response (
                    timestamp, reqResId, headers, content, contentLength, statusCode,
                    statusMsg, remoteHost, remoteIp, remotePort, localIp, localPort,
                    initiatorId, initiatorPkg, isTracker, deviceIdentifier
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent()
            conn.prepareStatement(sql).use { pstmt ->
                pstmt.setLong(1, response.timestamp)
                pstmt.setInt(2, response.reqResId)
                pstmt.setString(3, response.headers)
                pstmt.setString(4, response.content)
                pstmt.setInt(5, response.contentLength)
                pstmt.setInt(6, response.statusCode)
                pstmt.setString(7, response.statusMsg)
                pstmt.setString(8, response.remoteHost)
                pstmt.setString(9, response.remoteIp)
                pstmt.setInt(10, response.remotePort)
                pstmt.setString(11, response.localIp)
                pstmt.setInt(12, response.localPort)
                pstmt.setInt(13, response.initiatorId)
                pstmt.setString(14, response.initiatorPkg)
                pstmt.setBoolean(15, response.isTracker)
                pstmt.setString(16, response.deviceIdentifier) // Use deviceIdentifier from header
                pstmt.executeUpdate()
            }
        }
    }

    fun insertApp(app: TelemetryApp) {
        DriverManager.getConnection(JDBC_URL).use { conn ->
            val sql = """
        INSERT INTO app (
            packageName, label, versionName, versionCode, isInstalled, 
            isSystem, flags
        ) VALUES (?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
            conn.prepareStatement(sql).use { pstmt ->
                pstmt.setString(1, app.packageName)
                pstmt.setString(2, app.label)
                pstmt.setString(3, app.versionName)
                pstmt.setLong(4, app.versionCode)
                pstmt.setBoolean(5, app.isInstalled)
                pstmt.setBoolean(6, app.isSystem)
                pstmt.setInt(7, app.flags)
                pstmt.executeUpdate()
            }
        }
    }


    fun getEntityCount(tableName: String): Int {
        val sql = "SELECT COUNT(*) FROM $tableName"

        return try {
            DriverManager.getConnection(JDBC_URL).use { conn ->
                conn.createStatement().executeQuery(sql).use { resultSet ->
                    if (resultSet.next()) {
                        resultSet.getInt(1)
                    } else {
                        0
                    }
                }
            }
        } catch (e: Exception) {
            0 // Return 0 if an error occurs
        }
    }

    fun getUniqueDeviceIdentifierCount(tableName: String): Int {
        val sql = "SELECT COUNT(DISTINCT deviceIdentifier) FROM $tableName"

        return try {
            DriverManager.getConnection(JDBC_URL).use { conn ->
                conn.createStatement().executeQuery(sql).use { resultSet ->
                    if (resultSet.next()) {
                        resultSet.getInt(1)
                    } else {
                        0
                    }
                }
            }
        } catch (e: Exception) {
            0 // Return 0 if an error occurs
        }
    }


}
