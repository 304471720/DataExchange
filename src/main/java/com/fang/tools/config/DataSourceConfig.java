package com.fang.tools.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by user on 2017/7/12.
 */
@Configuration
public class DataSourceConfig {
    @Bean(name = "fromDataSource")
    @Qualifier("fromDataSource")
    @ConfigurationProperties(prefix="spring.datasource.from")
    public DataSource fromDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "toDataSource")
    @Qualifier("toDataSource")
    @Primary
    @ConfigurationProperties(prefix="spring.datasource.to")
    public DataSource toDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "fromJdbcTemplate")
    public JdbcTemplate fromJdbcTemplate(@Qualifier("fromDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "toJdbcTemplate")
    public JdbcTemplate  toJdbcTemplate(@Qualifier("toDataSource")DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
