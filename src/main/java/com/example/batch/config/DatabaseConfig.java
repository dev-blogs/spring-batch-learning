package com.example.batch.config;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@PropertySource("classpath:src/main/resources/application.properties")
public class DatabaseConfig {

    @Value("${databaseName}")
    private String databaseName;
    @Value("${host}")
    private String host;
    @Value("${login}")
    private String login;
    @Value("${password}")
    private String password;
    @Bean
    public DataSource dataSource() throws SQLException {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setDatabaseName(databaseName);
        dataSource.setServerName(host);
        dataSource.setPort(3306);
        dataSource.setUser(login);
        dataSource.setPassword(password);
        dataSource.setServerTimezone("UTC");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() throws SQLException {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public DataSourceTransactionManager transactionManager() throws SQLException {
        return new DataSourceTransactionManager(dataSource());
    }
}
