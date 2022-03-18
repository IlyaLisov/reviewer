package com.example.reviewer;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/employee/**").addResourceLocations("file:" + System.getProperty("user.dir") + "\\data\\employees\\");
        registry.addResourceHandler("/images/entity/**").addResourceLocations("file:" + System.getProperty("user.dir") + "\\data\\entities\\");
        registry.addResourceHandler("/images/user/**").addResourceLocations("file:" + System.getProperty("user.dir") + "\\data\\users\\");
    }
}
