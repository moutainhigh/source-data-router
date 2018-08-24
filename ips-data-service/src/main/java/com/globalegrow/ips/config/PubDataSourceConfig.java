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
@MapperScan(basePackages = "com.globalegrow.ips.mapper.pub", sqlSessionTemplateRef = "pubSqlSessionTemplate")
public class PubDataSourceConfig {
	@Bean(name = "pubDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.pub")
//    @Primary
	public DruidDataSource dbDataSource() {
		return DruidDataSourceBuilder.create().build();
	}

	@Bean(name = "pubSqlSessionFactory")
//    @Primary
	public SqlSessionFactory dbSqlSessionFactory(@Qualifier("pubDataSource") DruidDataSource dataSource)
			throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/pub/*.xml"));
		return bean.getObject();
	}

	@Bean(name = "pubTransactionManager")
//    @Primary
	public DataSourceTransactionManager dbTransactionManager(@Qualifier("pubDataSource") DruidDataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean(name = "pubSqlSessionTemplate")
//    @Primary
	public SqlSessionTemplate dbSqlSessionTemplate(
			@Qualifier("pubSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
}
