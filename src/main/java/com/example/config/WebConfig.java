package com.example.config;

import com.example.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns(
                        "/api/users/**",
                        "/api/topics/me/**",
                        "/api/topics/random",
                        "/api/topics/search/**",
                        "/api/topics/hot",
                        "/api/topics"
                )
                .excludePathPatterns(
                        "/api/auth/**",
                        "/api/topics/*",
                        "/api/topics/*/results",
                        "/api/vote",
                        "/api/ai/**",
                        "/api/comments/**",
                        "/test/**"
                );
    }
}
