package com.hiworldalexs.backend.spa

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

/**
 * This class is for Single Web Application (SPA). If separate build frontend(FE) and backend(BE), this class is meaningless
 * It is only applicable when the build bundling frontend and backend
 */
@ConditionalOnProperty(name = ["bundle"], havingValue = "true")
@Component
class SpaWebFilter : OncePerRequestFilter() {
    companion object {
        private const val BACKEND_URIS_PREFIX = "/api"
        private val WHITELISTED_URIS = listOf(
            "/actuator/health"
        )
    }

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.requestURI

        val isBackendRequest: (String) -> Boolean = { uri: String -> uri.startsWith(BACKEND_URIS_PREFIX) }
        val isStaticResourceWithFileExtensionRequest: (String) -> Boolean =
            { uri -> StringUtils.getFilenameExtension(uri)?.isNotEmpty() ?: false }
        val isWhitelistedRequest: (String) -> Boolean =
            { uri -> WHITELISTED_URIS.any { whitelistedUri -> uri == whitelistedUri } }

        // ensure that the frontend application can handle client-side routing and render the appropriate view based on the requested path
        if (!isBackendRequest(path) &&
            !isStaticResourceWithFileExtensionRequest(path) &&
            !isWhitelistedRequest(path)
        ) {
            request.getRequestDispatcher("/").forward(request, response)
            return
        }
        filterChain.doFilter(request, response)
    }
}