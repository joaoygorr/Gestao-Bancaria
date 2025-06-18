package br.com.gestao_bancaria.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
public class RequestLoggingConfig extends OncePerRequestFilter {

    private static final Logger genericFilterBean = LoggerFactory.getLogger(RequestLoggingConfig.class);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) {
        try {
            String route = request.getRequestURI();
            String method = request.getMethod();
            genericFilterBean.info("Route: {} | Method: {} ", method, route);
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            genericFilterBean.error("Exception: {}", ex.getMessage());
        }
    }
}