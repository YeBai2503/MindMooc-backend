package com.mindmooc.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindmooc.gateway.config.AuthConfig;
import com.mindmooc.gateway.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 认证全局过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    
    private final JwtConfig jwtConfig;
    private final AuthConfig authConfig;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        log.info("请求路径: {}", path);
        
        // 检查是否需要跳过认证
        if (isSkipPath(path)) {
            log.info("跳过认证: {}", path);
            return chain.filter(exchange);
        }
        
        // 获取 Token
        String token = getTokenFromRequest(request);
        
        if (token == null || token.isEmpty()) {
            log.warn("Token 不存在: {}", path);
            return unauthorizedResponse(exchange, "未授权，请先登录");
        }
        
        // 验证 Token
        try {
            Claims claims = validateToken(token);
            
            if (claims == null) {
                log.warn("Token 无效: {}", path);
                return unauthorizedResponse(exchange, "Token 无效或已过期");
            }
            
            // 从 Token 中提取用户信息
            String userId = claims.get("userId", String.class);
            String username = claims.get("username", String.class);
            
            // 将用户信息添加到请求头中，传递给下游服务
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-Username", username)
                    .build();
            
            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(modifiedRequest)
                    .build();
            
            log.info("认证成功: userId={}, username={}", userId, username);
            return chain.filter(modifiedExchange);
            
        } catch (Exception e) {
            log.error("Token 验证失败", e);
            return unauthorizedResponse(exchange, "Token 验证失败");
        }
    }
    
    /**
     * 检查路径是否需要跳过认证
     */
    private boolean isSkipPath(String path) {
        if (authConfig.getSkipPaths() == null) {
            return false;
        }
        
        for (String skipPath : authConfig.getSkipPaths()) {
            if (pathMatcher.match(skipPath, path)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 从请求中获取 Token
     */
    private String getTokenFromRequest(ServerHttpRequest request) {
        String authorization = request.getHeaders().getFirst("Authorization");
        
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        
        return null;
    }
    
    /**
     * 验证 Token
     */
    private Claims validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
            
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            // 检查是否过期
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                return null;
            }
            
            return claims;
        } catch (Exception e) {
            log.error("Token 解析失败", e);
            return null;
        }
    }
    
    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("message", message);
        result.put("data", null);
        
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(result);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("JSON 序列化失败", e);
            return response.setComplete();
        }
    }
    
    @Override
    public int getOrder() {
        return -100; // 优先级较高，确保在其他过滤器之前执行
    }
}

