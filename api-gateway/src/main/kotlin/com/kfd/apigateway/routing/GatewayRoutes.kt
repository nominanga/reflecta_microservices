package com.kfd.apigateway.routing

import com.kfd.apigateway.filters.JwtAuthFilter
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GatewayRoutes(
    private val jwtAuthFilter: JwtAuthFilter,
) {
    @Bean
    fun routes(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .route("auth-logout") {
                it.path("/api/auth/logout")
                    .filters { f -> f.filter(jwtAuthFilter) }
                    .uri("lb://authentication-service")
            }
            .route("auth-general") {
                it.path("/api/auth/**")
                    .uri("lb://authentication-service")
            }
            .route("media-upload-avatar") {
                it.path("/api/me/avatar")
                    .filters { f -> f.filters(jwtAuthFilter) }
                    .uri("lb://media-service")
            }
            .route("user-api") {
                it.path("/api/me/**")
                    .filters { f -> f.filter(jwtAuthFilter) }
                    .uri("lb://user-service")
            }
            .route("note-api") {
                it.path("/api/notes/**")
                    .filters { f -> f.filter(jwtAuthFilter) }
                    .uri("lb://note-service")
            }
            .route("media-public") {
                it.path("/media/**")
                    .uri("lb://media-service")
            }
            .build()
    }
}
