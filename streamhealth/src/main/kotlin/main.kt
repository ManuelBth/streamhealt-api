package com.betha

import com.betha.auth.repository.AuthRepository
import com.betha.auth.repository.AuthRepositoryImpl
import com.betha.auth.service.AuthService
import com.betha.auth.service.AuthServiceImpl
import com.betha.common.config.ConfigLoader
import com.betha.common.security.BcryptPasswordService
import com.betha.common.security.JWTService
import com.betha.common.security.JwtService
import com.betha.common.security.PasswordService
import com.betha.doctor.repository.DoctorRepository
import com.betha.doctor.repository.DoctorRepositoryImpl
import com.betha.doctor.service.DoctorService
import com.betha.doctor.service.DoctorServiceImpl
import com.betha.user.repository.UserRepository
import com.betha.user.repository.UserRepositoryImpl
import com.betha.user.service.UserService
import com.betha.user.service.UserServiceImpl
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureKoin()
        configureSerialization()
        configureSecurity()
        configureRouting()
    }.start(wait = true)
}

fun Application.configureKoin() {
    install(Koin) {
        // Módulos Koin para inyección de dependencias
        modules(
            appConfigModule,
            passwordModule,
            jwtModule,
            authModule,
            userModule,
            doctorModule
        )
    }
}

// Módulo de configuración de la aplicación
val appConfigModule = org.koin.dsl.module {
    single { ConfigLoader.load() }
}

// Módulo de servicio de password
val passwordModule = org.koin.dsl.module {
    single<PasswordService> { BcryptPasswordService() }
}

// Módulo de servicio JWT
val jwtModule = org.koin.dsl.module {
    single<JWTService> { JwtService() }
}

// Módulo de autenticación
val authModule = org.koin.dsl.module {
    single<AuthRepository> { AuthRepositoryImpl() }
    single<AuthService> { AuthServiceImpl(get(), get(), get()) }
}

// Módulo de usuario
val userModule = org.koin.dsl.module {
    single<UserRepository> { UserRepositoryImpl() }
    single<UserService> { UserServiceImpl(get()) }
}

// Módulo de doctor
val doctorModule = org.koin.dsl.module {
    single<DoctorRepository> { DoctorRepositoryImpl() }
    single<DoctorService> { DoctorServiceImpl(get(), get()) }
}