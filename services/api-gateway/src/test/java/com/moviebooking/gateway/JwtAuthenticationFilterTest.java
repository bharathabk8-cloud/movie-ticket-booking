package com.moviebooking.gateway;

import com.moviebooking.gateway.filter.JwtAuthenticationFilter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter filter;

    private GatewayFilter gatewayFilter;

    private static final String SECRET_KEY =
            "your-secret-key-for-jwt-token-generation-min-32-chars";

    @BeforeEach
    void setup() {
        filter = new JwtAuthenticationFilter();
        gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
    }

    private String generateToken() {

        return Jwts.builder()
                .setSubject("bharath")
                .signWith(SignatureAlgorithm.HS256,
                        SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    @Test
    void testValidJwtToken() {

        String token = generateToken();

        MockServerHttpRequest request =
                MockServerHttpRequest.get("/movies")
                        .header(HttpHeaders.AUTHORIZATION,
                                "Bearer " + token)
                        .build();

        MockServerWebExchange exchange =
                MockServerWebExchange.from(request);

        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        when(chain.filter(exchange)).thenReturn(Mono.empty());

        gatewayFilter.filter(exchange, chain).block();

        verify(chain).filter(exchange);
    }

    @Test
    void testNoAuthorizationHeader() {

        MockServerHttpRequest request =
                MockServerHttpRequest.get("/movies").build();

        MockServerWebExchange exchange =
                MockServerWebExchange.from(request);

        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        when(chain.filter(exchange)).thenReturn(Mono.empty());

        gatewayFilter.filter(exchange, chain).block();

        verify(chain).filter(exchange);
    }

    @Test
    void testAuthorizationHeaderWithoutBearer() {

        MockServerHttpRequest request =
                MockServerHttpRequest.get("/movies")
                        .header(HttpHeaders.AUTHORIZATION, "Basic xyz")
                        .build();

        MockServerWebExchange exchange =
                MockServerWebExchange.from(request);

        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        when(chain.filter(exchange)).thenReturn(Mono.empty());

        gatewayFilter.filter(exchange, chain).block();

        verify(chain).filter(exchange);
    }

    @Test
    void testInvalidJwtToken() {

        MockServerHttpRequest request =
                MockServerHttpRequest.get("/movies")
                        .header(HttpHeaders.AUTHORIZATION,
                                "Bearer invalid.jwt.token")
                        .build();

        MockServerWebExchange exchange =
                MockServerWebExchange.from(request);

        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        when(chain.filter(exchange)).thenReturn(Mono.empty());

        gatewayFilter.filter(exchange, chain).block();

        assertEquals(HttpStatus.UNAUTHORIZED,
                exchange.getResponse().getStatusCode());

        verify(chain, never()).filter(exchange);
    }

    @Test
    void testMalformedJwtToken() {

        MockServerHttpRequest request =
                MockServerHttpRequest.get("/movies")
                        .header(HttpHeaders.AUTHORIZATION,
                                "Bearer abc")
                        .build();

        MockServerWebExchange exchange =
                MockServerWebExchange.from(request);

        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        when(chain.filter(exchange)).thenReturn(Mono.empty());

        gatewayFilter.filter(exchange, chain).block();

        assertEquals(HttpStatus.UNAUTHORIZED,
                exchange.getResponse().getStatusCode());

        verify(chain, never()).filter(exchange);
    }

}