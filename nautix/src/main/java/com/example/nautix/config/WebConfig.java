package com.example.nautix.config;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig {

  @Value("${file.upload.product-images-dir}")
  private String productImagesDir;
  
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {

    String uploadPath = Paths
      .get(productImagesDir)
      .toAbsolutePath()
      .toUri()
      .toString();
      
    registry
      .addResourceHandler("/uploads/products/**")
      .addResourceLocations(uploadPath);
  }
  
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("http://localhost:*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
