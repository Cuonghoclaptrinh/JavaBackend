package com.example.PRJWEB.Configure;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Áp dụng cho tất cả đường dẫn
                .allowedOrigins("http://localhost:3000") // Cho phép React gọi
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Chỉ định phương thức cụ thể
                .allowedHeaders("*") // Cho phép mọi header
                .allowCredentials(true); // Cho phép gửi cookie/token nếu cần
    }
}