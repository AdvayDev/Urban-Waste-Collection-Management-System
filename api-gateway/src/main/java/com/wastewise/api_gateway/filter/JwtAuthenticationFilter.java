package com.wastewise.api_gateway.filter;

import com.wastewise.api_gateway.exception.UnauthorizationException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.List;

public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String secret;

    private static final List<String> excludedPaths = List.of( "/wastewise/login", "wastewise/internal/","/wastewise/validate");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain){

        String requestPath = exchange.getRequest().getURI().getPath();

        if(isExcludedPath(requestPath)){
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        try{
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (UnauthorizationException e){
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    private boolean isExcludedPath(String path){
        return excludedPaths.stream().anyMatch(path::startsWith);
    }

    @Override
    public int getOrder(){
        return -1;
    }
}
