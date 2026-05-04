package com.betha

import com.betha.auth.controller.authController
import com.betha.auth.service.AuthService
import com.betha.doctor.controller.doctorController
import com.betha.doctor.service.DoctorService
import com.betha.user.controller.userController
import com.betha.user.service.UserService
import io.ktor.http.HttpHeaders
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.openapi.openAPI
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    // Instalar CORS para Swagger UI
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
    }

    // Obtener servicios del contenedor Koin usando inject
    val authService: AuthService by inject()
    val userService: UserService by inject()
    val doctorService: DoctorService by inject()

    routing {
        // Ruta raíz
        get("/") {
            call.respondText("StreamHealth API - Betha Telemedicine Platform")
        }

        // Primero las rutas de la API
        authController(authService)
        userController(userService, authService)
        doctorController(doctorService, authService)

        // Swagger UI al final
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
        openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml")
    }
}