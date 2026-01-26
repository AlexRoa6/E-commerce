package com.alexr.ecommerce.config;

import com.alexr.ecommerce.service.CustomUserDetailsService;
import com.alexr.ecommerce.service.JwtService;
import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Obtener el header Authorization
        final String authHeader = request.getHeader("Authorization");
        // 2. Verificar que empiece con "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        // 3. Extraer el token (quitar "Bearer ")
        final String token = authHeader.substring(7);
        // 4. Extraer username del token
        final String username = jwtService.extractUsername(token);

        // 5. Verificar que hay username y que no esté ya autenticado
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Cargar el usuario de la BD
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 7. Validar el token
            if (jwtService.isTokenValid(token, userDetails)) {

                // 8. Autenticar al usuario en Spring Security
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // 9. Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
