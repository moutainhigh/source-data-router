package com.globalegrow.dy.config;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

/*
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(10);
        //threadPoolTaskScheduler.setRemoveOnCancelPolicy(true);
        return threadPoolTaskScheduler;
    }

    @Bean
    public ConcurrentHashMap<String, ScheduledFuture<?>> scheduleTaskContainer() {
        return new ConcurrentHashMap<>();
    }*/

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(/*httpRequestFactory()*/);
    }

    @Bean
    public ClientHttpRequestFactory httpRequestFactory() {
        HttpComponentsClientHttpRequestFactory componentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient());
        componentsClientHttpRequestFactory.setReadTimeout(20000);
        return componentsClientHttpRequestFactory;

    }

    @Bean
    public HttpClient httpClient() {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        //???????????????????????????????????? ???????????????????????????
        connectionManager.setMaxTotal(500);
        //????????????maxTotal?????????
        connectionManager.setDefaultMaxPerRoute(10);
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(20000) //?????????????????????(response)?????????????????????????????????read timeout
                .setConnectTimeout(20000)//??????????????????(????????????)?????????????????????????????????connect timeout
                .setConnectionRequestTimeout(20000)//?????????????????????????????????????????????????????????????????????????????????????????????org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for connection from pool
                .build();
        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .build();
    }

}
