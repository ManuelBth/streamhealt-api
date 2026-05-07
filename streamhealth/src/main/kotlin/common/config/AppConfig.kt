package com.betha.common.config

import io.ktor.server.config.*
import org.yaml.snakeyaml.Yaml
import java.io.File

/**
 * Application configuration loaded from application.yaml
 */
data class AppConfig(
    val jwt: JwtConfig,
    val mongodb: MongoDBConfig,
    val app: AppServerConfig
)

/**
 * JWT configuration
 */
data class JwtConfig(
    val secret: String,
    val expiration: Long // in milliseconds
) {
    companion object {
        const val DEFAULT_EXPIRATION = 24 * 60 * 60 * 1000L // 24 hours in ms
    }
}

/**
 * MongoDB configuration
 */
data class MongoDBConfig(
    val uri: String
)

/**
 * Application server configuration
 */
data class AppServerConfig(
    val host: String,
    val port: Int
)

/**
 * Loads configuration from application.yaml
 */
object ConfigLoader {
    private var config: AppConfig? = null

    /**
     * Load configuration from the application.yaml file
     */
    fun load(): AppConfig {
        config?.let { return it }

        val appConfig = loadConfig()


        config = appConfig
        return appConfig
    }

    private fun loadConfig(): AppConfig {

        val jwtSecret = "default-secret-change-in-production"
        val jwtExpiration = JwtConfig.DEFAULT_EXPIRATION
        val mongodbUri = "mongodb://localhost:27017"
        val host =  "0.0.0.0"
        val port = 8080

        return AppConfig(
            jwt = JwtConfig(jwtSecret, jwtExpiration),
            mongodb = MongoDBConfig(mongodbUri),
            app = AppServerConfig(host, port)
        )
    }


    /**
     * Get the current configuration instance
     */
    fun get(): AppConfig = config ?: load()
}