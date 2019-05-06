package com.globalegrow.dy.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisCacheConfig {

   /* @Bean
    public CacheManager cacheManager(@Qualifier("jscksonRedisTemplate") RedisTemplate redisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        cacheManager.setDefaultExpiration(86400L);
        cacheManager.setUsePrefix(true);
        Map<String, Long> expires = new HashMap<>();
        expires.put("bts_report_cache_2", 900L);
        expires.put("bts_report_data_cache", 900L);
        expires.put("listpage_report_cache", 900L);
        expires.put("listpage_report_count_cache", 900L);
        expires.put("goods_report_cache", 900L);
        expires.put("goods_report_count_cache", 900L);
        cacheManager.setExpires(expires);
        return cacheManager;
    }*/

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory, RedisCacheConfiguration redisCacheConfiguration) {

        Map<String, Long> expires = new HashMap<>();
        expires.put("bts_report_cache_2", 900L);
        expires.put("bts_report_data_cache", 900L);
        expires.put("listpage_report_cache", 900L);
        expires.put("listpage_report_count_cache", 900L);
        expires.put("goods_report_cache", 900L);
        expires.put("goods_report_count_cache", 900L);

        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(connectionFactory).cacheDefaults(redisCacheConfiguration).transactionAware();
        if (expires.size() > 0) {
            Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
            expires.forEach((k,v) -> {
                redisCacheConfigurationMap.put(k, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(v)));
            });
            builder
                    .withInitialCacheConfigurations(redisCacheConfigurationMap);
        }
        RedisCacheManager cacheManager = builder.build();
        return cacheManager;
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(){
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair
                .fromSerializer(jackson2JsonRedisSerializer);

        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(900)).serializeValuesWith(pair)
                .disableCachingNullValues();

        return redisCacheConfiguration;
    }

  /*  @Bean
    public RedisTemplate jscksonRedisTemplate(RedisConnectionFactory connectionFactory){
        StringRedisTemplate template = new StringRedisTemplate(connectionFactory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setKeySerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }*/

}
