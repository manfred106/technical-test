package org.test.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.test.interceptor.IpGeoLocationInterceptor;
import org.test.interceptor.RequestLogInterceptor;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public IpGeoLocationInterceptor getIpFilterInterceptor() {
        return new IpGeoLocationInterceptor();
    }

    @Bean
    public RequestLogInterceptor getRequestLogInterceptor() {
        return new RequestLogInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getRequestLogInterceptor());
        registry.addInterceptor(getIpFilterInterceptor());
    }

}