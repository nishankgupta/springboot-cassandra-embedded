package com.nishank.cassandra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.config.java.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.mapping.BasicCassandraMappingContext;
import org.springframework.data.cassandra.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

/**
 * Created by Nishank Gupta on 08-Jun-18.
 */
@Configuration
@EnableCassandraRepositories(basePackages = "com.nishank.cassandra.repository")
public class CassandraConfig extends AbstractCassandraConfiguration {
    @Override
    protected String getKeyspaceName() {
        return "demoKeySpace";
    }

    @Bean
    public CassandraClusterFactoryBean cluster() {
        CassandraClusterFactoryBean cassandraClusterFactoryBean = new CassandraClusterFactoryBean();
        cassandraClusterFactoryBean.setContactPoints("127.0.0.1");
        cassandraClusterFactoryBean.setPort(9142);

        return cassandraClusterFactoryBean;
    }

    @Bean
    public CassandraMappingContext context() {
        return new BasicCassandraMappingContext();
    }
}
