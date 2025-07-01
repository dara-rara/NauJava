package ru.glebova.NauJava.config;

import net.bull.javamelody.*;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class JavaMelodyConfiguration {

    @Bean
    public FilterRegistrationBean<MonitoringFilter> monitoringFilter() {
        FilterRegistrationBean<MonitoringFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new MonitoringFilter());
        registration.addUrlPatterns("/*");
        registration.setName("monitoring");
        registration.setInitParameters(getInitParameters());
        return registration;
    }

    private Map<String, String> getInitParameters() {
        Map<String, String> props = new HashMap<>();
        props.put("storage-directory", "/tmp/javamelody");
        props.put("authorized-users", "admin:admin");
        return props;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public MonitoringSpringAdvisor monitoringAdvisor() {
        MonitoringSpringAdvisor advisor = new MonitoringSpringAdvisor();
        advisor.setPointcut(new MonitoredWithAnnotationPointcut());
        return advisor;
    }

    @Bean
    public SpringDataSourceBeanPostProcessor dataSourceBeanPostProcessor() {
        return new SpringDataSourceBeanPostProcessor();
    }
}