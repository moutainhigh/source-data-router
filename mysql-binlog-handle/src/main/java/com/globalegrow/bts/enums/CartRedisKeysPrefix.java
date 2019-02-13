package com.globalegrow.bts.enums;

import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.Map;

public enum CartRedisKeysPrefix {

        dyGbCartInfo("dy_gb_cart_info_${userId}_{sku}"),dyZafulAppCartInfo("dy_zfapp_cart_info_${userId}_{sku}");

        private String redisKeyPrefix;

        public String getRedisKeyPrefix() {
                return redisKeyPrefix;
        }

        CartRedisKeysPrefix(String redisKeyPrefix) {
                this.redisKeyPrefix = redisKeyPrefix;
        }

        /**
         * 获取购物车 redis key
         * @param keysPrefix
         * @param userId
         * @param sku
         * @return
         */
        public static String redisCartKey(CartRedisKeysPrefix keysPrefix, String userId, String sku) {
                Map valuesMap = new HashMap();
                valuesMap.put("userId", userId);
                valuesMap.put("sku", sku);
                StringSubstitutor sub = new StringSubstitutor(valuesMap);
                return sub.replace(keysPrefix.getRedisKeyPrefix());
        }
}
