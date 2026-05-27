package co.edu.uco.mihorario.infrastructure.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            UsernamePasswordAuthenticationToken authentication = null;

            if ("token-valid-coordinador".equals(token)) {
                authentication = new UsernamePasswordAuthenticationToken(
                        "coordinador", null, List.of(new SimpleGrantedAuthority("ROLE_COORDINADOR"))
                );
            } else if ("token-invalid-empleado".equals(token)) {
                authentication = new UsernamePasswordAuthenticationToken(
                        "empleado", null, List.of(new SimpleGrantedAuthority("ROLE_EMPLEADO"))
                );
            }

            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
