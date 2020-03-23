package com.yonyou.guarantee.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;


/**
 * Spring Boot Application using Spring Data Jdbc.
 */
@Configuration
public class DataSourceConfiguration {

    @Bean("zbsDataSourceProperties")
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties zbsDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("oaDataSourceProperties")
    @ConfigurationProperties("spring.second-datasource")
    public DataSourceProperties oaDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * Create primary (default) DataSource.
     */
    @Bean("zbsDatasource")
    @Primary
    public DataSource zbsDataSource(@Autowired @Qualifier("zbsDataSourceProperties") DataSourceProperties props) {
        return props.initializeDataSourceBuilder().build();
    }

    /**
     * Create second DataSource and named "secondDatasource".
     */
    @Bean("oaDatasource")
    public DataSource oaDataSource(@Autowired @Qualifier("oaDataSourceProperties") DataSourceProperties props) {
        return props.initializeDataSourceBuilder().build();
    }

    /**
     * Create primary (default) JdbcTemplate from primary DataSource.
     */
    @Bean(name = "zbsJdbcTemplate")
    @Primary
    public JdbcTemplate zbsJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * Create second JdbcTemplate from second DataSource.
     */
    @Bean(name = "oaJdbcTemplate")
    public JdbcTemplate secondJdbcTemplate(@Autowired @Qualifier("oaDatasource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
