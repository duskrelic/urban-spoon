package com.pebblepost.cpp_sync.config;

import com.pebblepost.cpp_sync.aop.logging.LoggingAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.vault.config.EnvironmentVaultConfiguration;

@Configuration
@EnableAspectJAutoProxy
@Import(EnvironmentVaultConfiguration.class)
public class AppConfig {

  @Bean
  public LoggingAspect loggingAspect() {
    return new LoggingAspect();
  }

  @Bean
  public LocalValidatorFactoryBean validator() {
    return new LocalValidatorFactoryBean();
  }
}
