package io.dawn.ivrauto.config;

import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DBDataSource {

  @ConfigurationProperties(prefix = "spring.datasource")
  @Bean
  @Primary
  public static DataSource dbDataSource() {
    return DataSourceBuilder.create().build();
  }
}
