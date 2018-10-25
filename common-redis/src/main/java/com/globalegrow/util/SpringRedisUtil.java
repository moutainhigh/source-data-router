/**
 *
 */
package com.globalegrow.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangzhongfu on 2017/6/23.
 */
@Component
public class SpringRedisUtil {

    private static RedisTemplate<String, String> redistemplate;

    private static Logger logger = LoggerFactory.getLogger(SpringRedisUtil.class);


    private static String redisEnvKey = "";

    @Autowired
    public void setTemplate(RedisTemplate<String, String> template) {
        redistemplate = template;
        //redistemplate.setKeySerializer(new StringRedisSerializer());
    }

    @Value("${redis.env:''}")
    public void setEnvKey(String envKey) {
        redisEnvKey = envKey;
        logger.info("redis_env_key:{}", redisEnvKey);
    }

    public static <T>Map<String, T> boundHashOperations(String key, Class<T> clazz) {
        logger.debug("SpringRedisUtil_boundHashOperations_data_key:{}", key);
        String envRedisKey = getEnvRedisKey(key);
        logger.debug("SpringRedisUtil_boundHashOperations_data_envRedisKey:{}", envRedisKey);
        BoundHashOperations<String, String, T> boundHashOperations = redistemplate.boundHashOps(envRedisKey);

        Map<String, T> tMap = boundHashOperations.entries();
        logger.debug("SpringRedisUtil_boundHashOperations_data_size:{}", tMap.size());
        return tMap;
    }

    public static Set<String> SMEMBERS(String key) {
        return redistemplate.opsForSet().members(key);
    }

    public static void putSet(String key, String... values) {
        redistemplate.opsForSet().add(key, values);
    }

    /**
     * 根据前缀获取 key
     * @param prefix
     * @return
     */
    public static Set<String> getAllKeyByPrefix(String prefix) {
        return redistemplate.keys(prefix + "*");
    }

    public static String getStringValue(String key) {
        return redistemplate.boundValueOps(getEnvRedisKey(key)).get();
    }
    
    public static String getStringValue(String prefix,String key) {
        return redistemplate.boundValueOps(getEnvRedisKey(prefix+key)).get();
    }

    public static Map<String, String> getMapStringValue(String key) {
        return boundHashOperations(key, String.class);
    }

    public static Map<String, Object> getMapObjValue(String key) {
        return boundHashOperations(key, Object.class);
    }

    public static void del(String key) {
        redistemplate.delete(key);
    }
    
    public static void del(String prefix,String key) {
        redistemplate.delete(prefix+key);
    }
    
    public static void put(String key, String value) {
        redistemplate.opsForValue().set(getEnvRedisKey(key), value);
    }
    public static void put(String key, String value, long expireSeconds) {
        redistemplate.opsForValue().set(getEnvRedisKey(key), value, expireSeconds, TimeUnit.SECONDS);
    }
    
    /*public static void put(String prefix,String key, String value, long expireSeconds) {
        redistemplate.opsForValue().set(getEnvRedisKey(prefix+key), value, expireSeconds, TimeUnit.SECONDS);
    }*/
    /*public static void put(String prefix,String key, String value) {
        redistemplate.opsForValue().set(getEnvRedisKey(prefix+key), value);
    }*/

    public static void putMapValue(String key, Map<String, String> value, long expireSeconds) {
        redistemplate.opsForHash().putAll(getEnvRedisKey(key), value);
        redistemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
    }



    public static String getHashValue(String key, String valueKey) {
        return boundHashOperations(key, String.class).get(valueKey);
    }

    public static String getEnvRedisKey(String key) {
       /* if (StringUtils.isNotEmpty(redisEnvKey) && !"produce".equals(redisEnvKey)) {
           logger.debug("读取 redis 测试数据");
            return key + redisEnvKey;
        }*/
        return key;
    }

    private static void putLoginfo(String key, String value, Long expireSeconds) {
    	logger.info("put....==> key:"+key+",value:"+value+",expireSeconds:"+expireSeconds);
    }
    private static void putLoginfo(String key, String value) {
    	putLoginfo(key, value, null);
    }

}
