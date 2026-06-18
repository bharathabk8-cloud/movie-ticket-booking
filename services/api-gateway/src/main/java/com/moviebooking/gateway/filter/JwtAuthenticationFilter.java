package com.moviebooking.gateway.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * JWT Authentication filter for API Gateway
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private static final String SECRET_KEY = "your-secret-key-for-jwt-token-generation-min-32-chars";
    private static final String BEARER_PREFIX = "Bearer ";

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerWebExchange modifiedExchange = exchange;
            
            try {
                String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                
                if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
                    String token = authHeader.substring(BEARER_PREFIX.length());
                    
                    // Validate JWT token
                    Jwts.parser()
                            .setSigningKey(SECRET_KEY.getBytes())
                            .parseClaimsJws(token);
                    
                    log.debug("JWT token validated successfully");
                }
            } catch (SignatureException e) {
                log.error("Invalid JWT signature: {}", e.getMessage());
                return handleAuthenticationError(exchange, "Invalid JWT signature");
            } catch (Exception e) {
                log.error("JWT validation failed: {}", e.getMessage());
                // Proceed without authentication for public endpoints
            }
            
            return chain.filter(modifiedExchange);
        };
    }

    private Mono<Void> handleAuthenticationError(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
    }
}