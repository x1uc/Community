package com.example.gateway.filter;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.nacos.api.remote.request.ServerRequest;
import com.example.gateway.config.AuthProperties;
import com.example.gateway.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final AuthProperties authProperties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (isExclude(request.getPath().toString())) {
            return chain.filter(exchange);
        }
        
        List<String> tokenCollection = exchange.getRequest().getHeaders().get("token");
        String token = null;
        if (!(tokenCollection == null) && !tokenCollection.isEmpty()) {
            token = tokenCollection.get(0);
        }
        Long userId = null;
        userId = JwtTool.verifyToken(token);
        if (userId == null) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        String info = userId.toString();
        ServerWebExchange build = exchange.mutate().request(b -> b.header("user-id", info)).build();
        return chain.filter(build);
    }

    private boolean isExclude(String path) {
        for (String pattern : authProperties.getExclude()) {
            if (antPathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
