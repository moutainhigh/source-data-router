package com.globalegrow.dy.enums;

public enum AppEventEnums {
    /**
     * 曝光事件
     */
    af_impression {
        @Override
        public String map() {
            return "skuExposure";
        }

    },
    /**
     * 点击事件
     */
    af_view_product {
        @Override
        public String map() {
            return "skuHit";
        }


    },
    /**
     * 加购事件
     */
    af_add_to_bag {
        @Override
        public String map() {
            return "skuCart";
        }


    },
    /**
     * 收藏
     */
    af_add_to_wishlist {
        @Override
        public String map() {
            return "skuMarked";
        }


    },
    /**
     * 创建订单-下单
     */
    af_create_order_success {
        @Override
        public String map() {
            return "orders";
        }


    },
    /**
     * 搜索事件，只取normal search
     */
    af_search {
        @Override
        public String map() {
            return "skuSearchWord";
        }


    },
    /**
     * 订单支付成功
     */
    af_purchase {
        @Override
        public String map() {
            return "skuOrderPay";
        }


    };

    public abstract String map();

    //public abstract void handleEventResult(UserActionDto userActionDto, List<UserActionEsDto> data);

    /**
     * 根据传入事件名转换为 es 存储事件名
     * @param localName
     * @return
     */
    public static String getLogEventNameByLocalName(String localName) {
        for (AppEventEnums value : AppEventEnums.values()) {
            if (value.map().equals(localName)) {
                return value.name();
            }
        }
        return "";
    }


}
