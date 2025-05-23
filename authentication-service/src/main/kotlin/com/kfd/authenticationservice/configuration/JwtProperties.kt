package com.kfd.authenticationservice.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "jwt")
data class JwtProperties (
    var access: Access = Access(),
    var refresh: Refresh = Refresh(),
) {
    data class Access(
        var secret: String = "",
        var expirationTime: Long = 0,
    )
    data class Refresh(
        var secret: String = "",
        var expirationTime: Long = 0,
    )
}
