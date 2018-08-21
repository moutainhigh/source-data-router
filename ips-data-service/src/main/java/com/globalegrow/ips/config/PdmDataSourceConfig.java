package com.globalegrow.ips.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;


@Configuration
@MapperScan(basePackages = "com.globalegrow.ips.mapper.pdm", sqlSessionTemplateRef  = "pdmSqlSessionTemplate")
public class PdmDataSourceConfig {
    @Bean(name = "pdmDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.pdm")
    @Primary
    public DruidDataSource dbDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "pdmSqlSessionFactory")
    @Primary
    public SqlSessionFactory dbSqlSessionFactory(@Qualifier("pdmDataSource") DruidDataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/pdm/*.xml"));
        return bean.getObject();
    }

    @Bean(name = "pdmTransactionManager")
    @Primary
    public DataSourceTransactionManager dbTransactionManager(@Qualifier("pdmDataSource") DruidDataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "pdmSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate dbSqlSessionTemplate(@Qualifier("pdmSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
