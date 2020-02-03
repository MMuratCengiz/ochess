package com.mcp.ochess.config;

import org.postgresql.jdbc2.optional.PoolingDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;
import java.util.Properties;


@EnableWebMvc
@Configuration
@ComponentScan( { "com.mcp.ochess.*" } )
@EnableTransactionManagement
@Import({ OChessWebSecurityConfigurerAdapter.class })
public class HibernateConfig  {
    @Bean(name = "entityManagerFactory")
    public LocalSessionFactoryBean getSessionFactory() {
        LocalSessionFactoryBean bean = new LocalSessionFactoryBean();
        bean.setDataSource(getDataSource());
        bean.setPackagesToScan("com.mcp.ochess");
        bean.setHibernateProperties(getProperties());
        return bean;
    }

    private Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL10Dialect");
        return properties;
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager getTransactionManager() {
        HibernateTransactionManager manager = new HibernateTransactionManager();
        manager.setSessionFactory(getSessionFactory().getObject());
        return manager;
    }

    @Bean(name = "dataSource")
    public DataSource getDataSource() {
        PoolingDataSource dataSource = new PoolingDataSource();
        dataSource.setUser("xamsmgss");
        dataSource.setPassword("hf1ReW9og15G56wwJ7g3vIMBvNt0y8jv");
        dataSource.setServerName("manny.db.elephantsql.com");
        dataSource.setDatabaseName("xamsmgss");
        return dataSource;
    }
}

