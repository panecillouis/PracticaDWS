package com.godfathercapybara.capybara.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/error/404").setViewName("error/404");

    }

    @Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	    registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    	registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
    	registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
    	registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");
        registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/static/favicon.ico");
        registry.addResourceHandler("/style.css").addResourceLocations("classpath:/static/style.css");
        registry.addResourceHandler("/users/static/**").addResourceLocations("classpath:/static/");
    	registry.addResourceHandler("/users/css/**").addResourceLocations("classpath:/static/css/");
    	registry.addResourceHandler("/users/js/**").addResourceLocations("classpath:/static/js/");
    	registry.addResourceHandler("/users/images/**").addResourceLocations("classpath:/static/images/");
        registry.addResourceHandler("/users/favicon.ico").addResourceLocations("classpath:/static/favicon.ico");
        registry.addResourceHandler("/users/style.css").addResourceLocations("classpath:/static/style.css");
        
    }
    }
    


