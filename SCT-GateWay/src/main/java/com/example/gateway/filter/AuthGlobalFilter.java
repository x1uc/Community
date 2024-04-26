package com.example.gateway.filter;

import cn.hutool.core.collection.CollUtil;
import com.example.gateway.utils.JwtTool;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //1. 首先过滤 不需要过滤的请求地址
        //待完成


        //2. 校验请求头中的Token
        //   为空，直接返回
        //   非空，进行jwt判断
        List<String> tokenCollection = exchange.getRequest().getHeaders().get("token");

        if (CollUtil.isEmpty(tokenCollection)) {
            return chain.filter(exchange);
//            ServerHttpResponse response = exchange.getResponse();
//            response.setRawStatusCode(401);
//            return response.setComplete();
        }

        String token = tokenCollection.get(0);
        Long userId = null;
        userId = JwtTool.verifyToken(token);
        if (userId == null)
            return chain.filter(exchange);
        String info = userId.toString();

        ServerWebExchange build = exchange.mutate().request(b -> b.header("user-id", info)).build();
        return chain.filter(build);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
