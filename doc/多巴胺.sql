SELECT
CASE WHEN sum(EXPOSURE_COUNT) > 0 THEN ROUND((CAST(sum(GOOD_CLICK) AS FLOAT) / SUM(EXPOSURE_COUNT)) * 100, 3) ELSE 0 END
SUM_EXP_CLICK_RATE,
CASE WHEN sum(EXPOSURE_COUNT) > 0 THEN ROUND((CAST(sum(SALES_AMOUNT) AS FLOAT) / SUM(EXPOSURE_COUNT)) * 100, 3) ELSE 0 END
SUM_GOOD_BUY_TRANS_RATE,
CASE WHEN SUM(EXPOSURE_COUNT) > 0 THEN ROUND((CAST(sum(GOOD_ADD_CART) AS FLOAT) / SUM(EXPOSURE_COUNT)) * 100, 3) ELSE 0 END
SUM_GOOD_CART_RATE,
CASE WHEN sum(GOOD_ADD_CART) > 0 THEN ROUND((CAST(SUM(ORDER_SKU) AS FLOAT) / sum(GOOD_ADD_CART)) * 100, 3) ELSE 0 END
SUM_GOOD_ORDER_RATE,
CASE WHEN COUNT(DISTINCT GOOD_VIEW_UV) > 0 THEN ROUND((CAST(COUNT(DISTINCT GOOD_ADD_CART_UV) AS FLOAT) / COUNT(DISTINCT GOOD_VIEW_UV)) * 100, 3) ELSE 0 END
SUM_USER_CART_RATE,
CASE WHEN COUNT(DISTINCT GOOD_VIEW_UV) > 0 THEN ROUND((CAST(COUNT(DISTINCT GOOD_CLICK_UV) AS FLOAT) / COUNT(DISTINCT GOOD_VIEW_UV)) * 100, 3) ELSE 0 END
SUM_USER_CLICK_RATE,
CASE WHEN COUNT(DISTINCT GOOD_VIEW_UV) > 0 THEN ROUND((CAST(COUNT(DISTINCT ORDER_UV) AS FLOAT) / COUNT(DISTINCT GOOD_VIEW_UV)) * 100, 3) ELSE 0 END
SUM_USER_ORDER_RATE,
CASE WHEN COUNT(DISTINCT GOOD_VIEW_UV) > 0 THEN ROUND((CAST(COUNT(DISTINCT PAID_UV) AS FLOAT) / COUNT(DISTINCT GOOD_VIEW_UV)) * 100, 3) ELSE 0 END
SUM_USER_BUY_RATE, COUNT(DISTINCT specimen) specimen, COUNT(DISTINCT GOOD_VIEW_UV) SUM_GOOD_VIEW_UV,
COUNT(DISTINCT ORDER_UV) SUM_ORDER_UV,
SUM(AMOUNT) SUM_AMOUNT,
COUNT(DISTINCT GOOD_COLLECT_UV) SUM_GOOD_COLLECT_UV,
SUM(ORDER_SKU) SUM_ORDER_SKU,
COUNT(DISTINCT PAID_UV) SUM_PAID_UV,
SUM(EXPOSURE_COUNT) SUM_EXPOSURE_COUNT,
COUNT(DISTINCT GOOD_ADD_CART_UV) SUM_GOOD_ADD_CART_UV,
SUM(GOOD_CLICK) SUM_GOOD_CLICK, SUM(SALES_AMOUNT)
SUM_SALES_AMOUNT, SUM(GOOD_ADD_CART) SUM_GOOD_ADD_CART,
SUM(WHOLE_ORDER_AMOUNT) SUM_WHOLE_ORDER_AMOUNT,
COUNT(DISTINCT WHOLE_PAID_UV) SUM_WHOLE_PAID_UV,
COUNT(DISTINCT PAID_ORDER) SUM_PAID_ORDER,
SUM(WHOLE_AMOUNT)
SUM_WHOLE_AMOUNT,
SUM(GOOD_COLLECT) SUM_GOOD_COLLECT,
SUM(ORDER_AMOUNT) SUM_ORDER_AMOUNT,
COUNT(DISTINCT WHOLE_ORDER_UV)
SUM_WHOLE_ORDER_UV,
COUNT(DISTINCT GOOD_CLICK_UV) SUM_GOOD_CLICK_UV,
COUNT(DISTINCT "ORDER") SUM_ORDER, bts_planid,
day_start
FROM BTS_APP_DOPAMINE
WHERE bts_planid = '118'
GROUP BY bts_planid, day_start
ORDER BY day_start DESC;