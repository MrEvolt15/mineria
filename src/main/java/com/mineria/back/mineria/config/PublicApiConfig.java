package com.mineria.back.mineria.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PublicApiConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Endpoints públicos - Accesibles desde internet
        registry.addMapping("/api/realtime/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
                
        // Endpoints de administración - Solo acceso local
        registry.addMapping("/api/admin/**")
                .allowedOrigins("http://localhost:*", "http://127.0.0.1:*")
                .allowedMethods("GET")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
                
        // Endpoints CRUD antiguos - Solo acceso local (por compatibilidad)
        registry.addMapping("/api/personas/**")
                .allowedOrigins("http://localhost:*", "http://127.0.0.1:*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
                
        registry.addMapping("/api/camaras/**")
                .allowedOrigins("http://localhost:*", "http://127.0.0.1:*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
                
        registry.addMapping("/api/persona-camara/**")
                .allowedOrigins("http://localhost:*", "http://127.0.0.1:*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}
