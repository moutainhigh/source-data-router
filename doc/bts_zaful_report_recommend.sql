select
sum(exp_num) exp_num,
sum(click_num) click_num,
sum(ADD_CART_NUM) cart_num,
sum(sku_order_num) sku_order,
sum(paid_order_num) paid_order,
sum(paid_amount) paid_amount,
count(distinct specimen) specimen,
CASE
WHEN sum(paid_amount) > 0
THEN ROUND(CAST(sum(paid_amount) AS FLOAT) / 100, 2)
ELSE 0
END amount,
CASE
WHEN sum(exp_num) > 0
THEN ROUND(CAST(sum(click_num) AS FLOAT) / sum(exp_num), 4)
ELSE 0
END exp_rate,
CASE
WHEN sum(click_num) > 0
THEN ROUND(CAST(sum(ADD_CART_NUM) AS FLOAT) / sum(click_num), 4)
ELSE 0
END click_rate,
CASE
WHEN sum(ADD_CART_NUM) > 0 THEN ROUND(CAST(sum(sku_order_num) AS FLOAT) / sum(ADD_CART_NUM), 4)
ELSE 0
END order_rate,
CASE
WHEN sum(sku_order_num) > 0 THEN ROUND(CAST(sum(paid_order_num) AS FLOAT) / sum(sku_order_num), 4)
ELSE 0
END pay_rate,
CASE
WHEN sum(exp_num)  > 0 THEN ROUND(CAST(sum(paid_order_num) AS FLOAT) / sum(exp_num) , 3)
ELSE 0
END total_rate,
day_start,
bts_plan_id,
bts_version_id,
bts_bucket_id
from BTS_ZAFUL_RECOMMEND_REPORT
WHERE day_start = date '2018-08-06' and bts_plan_id = '13'
group by day_start,
bts_plan_id,
bts_version_id,
bts_bucket_id
order by day_start desc