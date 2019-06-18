# 用户行为数据实时查询
## 打包
clean install -Dmaven.test.skip=true -e -Pprod

## 用户行文涉及到的事件
af_view_product,af_add_to_bag,af_add_to_wishlist,af_create_order_success,af_search,af_purchase

## 接口文档
http://wiki.hqygou.com:8090/pages/viewpage.action?pageId=88638111
## 对应 es 索引
|索引名|别名|备注|
|---|---|---|
|dy_gb_user_event_limit_1000_6|dy_app_gb_event_realtime|gb app 用户行为,只取最近 1000 条,多于 1000 条移除最早的事件|
|dy_gb_user_base_info_limit_event|dy_app_gb_user_base|用户基本信息,从埋点数据中获取|
|dy_gb_search_words_skus_rel|无|gb 搜索词与 sku 对应关系|
|dy_app_gb_event-af_view_product|dy_app_gb|gb 用户行为事件 T+1 商品点击|
|dy_app_gb_event-af_search|dy_app_gb|gb 用户行为事件 T+1 搜索|
|dy_app_gb_event-af_purchase|dy_app_gb|gb 用户行为事件 T+1 支付成功|
|dy_app_gb_event-af_create_order_success|dy_app_gb|gb 用户行为事件 T+1 下单成功|
|dy_app_gb_event-af_add_to_wishlist|dy_app_gb|gb 用户行为事件 T+1 加收藏|
|dy_app_gb_event-af_add_to_bag|dy_app_gb|gb 用户行为事件 T+1 加入购物车|

## 对应 redis key 前缀
dy_rt_p_