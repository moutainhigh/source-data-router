package com.globalegrow.dy.enums;

import com.globalegrow.dy.dto.UserActionData;
import com.globalegrow.dy.dto.UserActionDto;
import com.globalegrow.dy.dto.UserActionEsDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum AppEventEnums {
    /**
     * 曝光事件
     */
    af_impression{
        @Override
        public String map() {
            return "skuExposure";
        }

        @Override
        public void handleEventResult(UserActionDto userActionDto, List<UserActionEsDto> data) {
            userActionDto.setSkuExposure(data.stream().map(d -> new UserActionData(d.getEvent_value(), d.getTimestamp())).collect(Collectors.toList()));
        }
    },
    /**
     * 点击事件
     */
    af_view_product{
        @Override
        public String map() {
            return "skuHit";
        }

        @Override
        public void handleEventResult(UserActionDto userActionDto, List<UserActionEsDto> data) {
            userActionDto.setSkuHit(data.stream().map(d -> new UserActionData(d.getEvent_value(), d.getTimestamp())).collect(Collectors.toList()));
        }
    },
    /**
     * 加购事件
     */
    af_add_to_bag{
        @Override
        public String map() {
            return "skuCart";
        }

        @Override
        public void handleEventResult(UserActionDto userActionDto, List<UserActionEsDto> data) {
            userActionDto.setSkuCart(data.stream().map(d -> new UserActionData(d.getEvent_value(), d.getTimestamp())).collect(Collectors.toList()));
        }
    },
    /**
     * 收藏
     */
    af_add_to_wishlist{
        @Override
        public String map() {
            return "skuMarked";
        }

        @Override
        public void handleEventResult(UserActionDto userActionDto, List<UserActionEsDto> data) {
            userActionDto.setSkuMarked(data.stream().map(d -> new UserActionData(d.getEvent_value(), d.getTimestamp())).collect(Collectors.toList()));
        }
    },
    /**
     * 创建订单-下单
     */
    af_create_order_success{
        @Override
        public String map() {
            return "orders";
        }

        @Override
        public void handleEventResult(UserActionDto userActionDto, List<UserActionEsDto> data) {
            userActionDto.setOrders(data.stream().map(d -> new UserActionData(d.getEvent_value(), d.getTimestamp())).collect(Collectors.toList()));
        }
    },
    /**
     * 搜索事件，只取normal search
     */
    af_search{
        @Override
        public String map() {
            return "skuSearchWord";
        }

        @Override
        public void handleEventResult(UserActionDto userActionDto, List<UserActionEsDto> data) {
            userActionDto.setSkuSearchWord(data.stream().map(d -> new UserActionData(d.getEvent_value(), d.getTimestamp())).collect(Collectors.toList()));
        }
    }
    ;

   public abstract String map();
   public abstract void handleEventResult(UserActionDto userActionDto, List<UserActionEsDto> data);

    public static String getLogEventNameByLocalName(String localName) {
        for (AppEventEnums value : AppEventEnums.values()) {
            if (value.map().equals(localName)) {
                return value.name();
            }
        }
        return "";
    }

}
