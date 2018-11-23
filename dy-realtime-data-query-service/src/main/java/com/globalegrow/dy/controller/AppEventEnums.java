package com.globalegrow.dy.controller;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public enum AppEventEnums {
    /**
     * 曝光事件
     */
    af_impression {
        @Override
        public String getEventValueFromEventValue(Map<String, Object> eventValueMap) {
            if (eventValueMap != null) {
                return String.valueOf(eventValueMap.get("af_content_id"));
            }
            return null;
        }
    },
    /**
     * 点击事件
     */
    af_view_product {
        @Override
        public String getEventValueFromEventValue(Map<String, Object> eventValueMap) {
            if (eventValueMap != null) {
                return String.valueOf(eventValueMap.get("af_content_id"));
            }
            return null;
        }
    },
    /**
     * 加购事件
     */
    af_add_to_bag {
        @Override
        public String getEventValueFromEventValue(Map<String, Object> eventValueMap) {
            if (eventValueMap != null) {
                return String.valueOf(eventValueMap.get("af_content_id"));
            }
            return null;
        }
    },
    /**
     * 收藏
     */
    af_add_to_wishlist {
        @Override
        public String getEventValueFromEventValue(Map<String, Object> eventValueMap) {
            if (eventValueMap != null) {
                return String.valueOf(eventValueMap.get("af_content_id"));
            }
            return null;
        }
    },
    /**
     * 创建订单-下单
     */
    af_create_order_success {
        @Override
        public String getEventValueFromEventValue(Map<String, Object> eventValueMap) {
            if (eventValueMap != null) {
                return String.valueOf(eventValueMap.get("af_content_id"));
            }
            return null;
        }
    },
    /**
     * 搜索事件，只取normal search
     */
    af_search {
        @Override
        public String getEventValueFromEventValue(Map<String, Object> eventValueMap) {
            if (eventValueMap != null) {
                if (eventValueMap.get("af_search_page") == null) {
                    if (eventValueMap.get("af_content_type") != null) {
                        return String.valueOf(eventValueMap.get("af_content_type"));
                    }
                    if (eventValueMap.get("af_search_string") != null) {
                        return String.valueOf(eventValueMap.get("af_search_string"));
                    }
                }
                if ("normal search".equals(String.valueOf(eventValueMap.get("af_search_page")))) {
                    return String.valueOf(eventValueMap.get("af_content_type"));
                }
            }
            return null;
        }
    }, af_purchase {
        @Override
        public String getEventValueFromEventValue(Map<String, Object> eventValueMap) {
            if (eventValueMap != null) {
                String skuString = String.valueOf(eventValueMap.get("af_content_id"));
                String priceString = String.valueOf(eventValueMap.get("af_price"));
                String amount = String.valueOf(eventValueMap.get("af_revenue"));
                if (StringUtils.isNotEmpty(skuString) && StringUtils.isNotEmpty(priceString) && StringUtils.isNotEmpty(amount)
                        && !"null".equals(skuString) && !"null".equals(priceString) && !"null".equals(amount)) {
                    String[] skus = skuString.split(",");
                    String[] prices = priceString.split(",");
                    if (skus.length == prices.length) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < skus.length; i++) {
                            String value = skus[i] + "_" + prices[i] + "_" + amount;
                            if (i == (skus.length - 1)) {
                                stringBuilder.append(value);
                            } else {
                                stringBuilder.append(value + ",");
                            }
                        }
                        return stringBuilder.toString();
                    }
                }
            }
            return null;
        }
    };

    public abstract String getEventValueFromEventValue(Map<String, Object> eventValueMap);
}
