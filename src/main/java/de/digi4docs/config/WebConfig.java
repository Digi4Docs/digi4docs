package de.digi4docs.config;

import de.digi4docs.JdbcDriverListener;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {
    //Register JdbcDriverListener
    @Bean
    public ServletListenerRegistrationBean<JdbcDriverListener> sessionCountListener() {
        ServletListenerRegistrationBean<JdbcDriverListener> listenerRegBean = new ServletListenerRegistrationBean<>();
        listenerRegBean.setListener(new JdbcDriverListener());
        return listenerRegBean;
    }
}
