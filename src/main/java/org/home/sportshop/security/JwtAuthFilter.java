package org.home.sportshop.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            
            // Проверка на пустой токен или отсутствие необходимых разделителей
            if (jwt == null || jwt.trim().isEmpty() || !jwt.contains(".")) {
                filterChain.doFilter(request, response);
                return;
            }
            
            try {
                username = jwtUtil.extractUsername(jwt);
                // Если JWT валиден и мы смогли извлечь имя пользователя, устанавливаем аутентификацию
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    Claims claims = jwtUtil.extractAllClaims(jwt);
                    
                    // Извлекаем роли из токена
                    List<String> roles = new ArrayList<>();
                    Object rolesObj = claims.get("roles");
                    if (rolesObj instanceof List<?>) {
                        List<?> rolesList = (List<?>) rolesObj;
                        // Safely convert each element to String if possible
                        for (Object item : rolesList) {
                            if (item instanceof String) {
                                roles.add((String) item);
                            }
                        }
                    }
                    
                    // Создаем список авторитетов из ролей
                    Collection<GrantedAuthority> authorities = roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList());
                    
                    // Создаем объект аутентификации
                    UsernamePasswordAuthenticationToken authToken = 
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    
                    // Устанавливаем аутентификацию в контекст
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                // Если токен не валиден, просто продолжаем без аутентификации
                logger.debug("JWT authentication failed for token: " + jwt.substring(0, Math.min(10, jwt.length())) + "...", e);
            }
        }
        
        filterChain.doFilter(request, response);
    }
} 