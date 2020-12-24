package com.pebblepost.cpp_sync.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.vault.annotation.VaultPropertySource;

import javax.sql.DataSource;

@Configuration
@VaultPropertySource(value = "${vault.path}/postgres")
public class PostgresConfig {

  private static final Logger log = LoggerFactory.getLogger(PostgresConfig.class);

  private final String host;
  private final String database;
  private final String username;
  private final String password;

  public PostgresConfig(Environment env) {
    this.host = env.getProperty("host");
    this.database = env.getProperty("database");
    this.username = env.getProperty("username");
    this.password = env.getProperty("password");
  }

  @Bean
  public DataSource getDataSource() {
    DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
    dataSourceBuilder
        .url(String.format("jdbc:postgresql://%s/%s", host, database))
        .username(username)
        .password(password);
    return dataSourceBuilder.build();
  }

  @Override
  public String toString() {
    return "PostgresConfig{"
        + "host='"
        + host
        + '\''
        + ", database='"
        + database
        + '\''
        + ", username='"
        + username
        + '\''
        + ", password='"
        + password
        + '\''
        + '}';
  }
}
