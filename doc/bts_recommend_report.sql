select
c.exp_num,
d.click_num,
f.cart_num,
g.order_num,
h.porder_num,
CASE
WHEN i.amount > 0
THEN ROUND(CAST(i.amount AS FLOAT) / 100, 2)
ELSE 0
END amount,
CASE
WHEN c.exp_num > 0
THEN ROUND(CAST(d.click_num AS FLOAT) / c.exp_num, 4)
ELSE 0
END exp_rate,
CASE
WHEN d.click_num > 0
THEN ROUND(CAST(f.cart_num AS FLOAT) / d.click_num, 4)
ELSE 0
END click_rate,
CASE
WHEN f.cart_num > 0 THEN ROUND(CAST(g.order_num AS FLOAT) / f.cart_num, 4)
ELSE 0
END order_rate,
CASE
WHEN g.order_num > 0 THEN ROUND(CAST(h.porder_num AS FLOAT) / g.order_num, 4)
ELSE 0
END pay_rate,
CASE
WHEN c.exp_num  > 0 THEN ROUND(CAST(h.porder_num AS FLOAT) / c.exp_num , 3)
ELSE 0
END total_rate,
c.DAY_START,
c.BTS_PLAN_ID,
c.BTS_VERSION_ID,
c.BTS_BUCKET_ID
from
(
  SELECT
  sum(EXPOSURE_COUNT) AS exp_num,
  DAY_START,
  BTS_PLAN_ID,
  BTS_VERSION_ID,
  BTS_BUCKET_ID
  FROM
  BTS_EXP_REPORT
  WHERE
  glb_plf = 'pc' AND glb_b = 'c' AND glb_mrlc = 'T_3'
  AND is_exposure = 1
  AND DAY_START = date '2018-08-18'
  AND BTS_PLAN_ID = '13'
  group by
  DAY_START, BTS_PLAN_ID, BTS_VERSION_ID, BTS_BUCKET_ID
) c

  left join
  (
    SELECT
    count(*) AS click_num,
    DAY_START,
    BTS_PLAN_ID,
    BTS_VERSION_ID,
    BTS_BUCKET_ID
    FROM
    BTS_CLICK_REPORT
    WHERE
    glb_plf = 'pc'
    AND glb_b = 'c'
    AND glb_mrlc = 'T_3'
    AND DAY_START = date '2018-08-18'
    AND BTS_PLAN_ID = '13'
    AND is_click = 1
    group by
    DAY_START, BTS_PLAN_ID, BTS_VERSION_ID, BTS_BUCKET_ID
  ) d
    on
  c.DAY_START = d.DAY_START and c.BTS_PLAN_ID = d.BTS_PLAN_ID and c.BTS_VERSION_ID = d.BTS_VERSION_ID and
  c.BTS_BUCKET_ID = d.BTS_BUCKET_ID

  left join
  (
    SELECT
    count(*) AS cart_num,
    DAY_START,
    BTS_PLAN_ID,
    BTS_VERSION_ID,
    BTS_BUCKET_ID
    FROM
    BTS_CART_REPORT
    WHERE
    glb_plf = 'pc'
    -- AND glb_b = 'c'
    AND glb_fmd = 'mr_T_3'
    AND DAY_START = date '2018-08-18'
    AND BTS_PLAN_ID = '13'
    AND is_cart = 1
    group by
    DAY_START, BTS_PLAN_ID, BTS_VERSION_ID, BTS_BUCKET_ID
  ) f
    on
  c.DAY_START = f.DAY_START and c.BTS_PLAN_ID = f.BTS_PLAN_ID and c.BTS_VERSION_ID = f.BTS_VERSION_ID and
  c.BTS_BUCKET_ID = f.BTS_BUCKET_ID

  left join
  (
    SELECT
    count(*) AS order_num,
    DAY_START,
    BTS_PLAN_ID,
    BTS_VERSION_ID,
    BTS_BUCKET_ID
    FROM
    BTS_ORDER_REPORT
    WHERE
    glb_plf = 'pc'
    -- AND glb_b = 'c'
    AND glb_fmd = 'mr_T_3'
    AND DAY_START = date '2018-08-18'
    AND BTS_PLAN_ID = '13'
    AND ORDER_STATUS = '0'
    group by
    DAY_START, BTS_PLAN_ID, BTS_VERSION_ID, BTS_BUCKET_ID
  ) g
    on
  c.DAY_START = g.DAY_START and c.BTS_PLAN_ID = g.BTS_PLAN_ID and c.BTS_VERSION_ID = g.BTS_VERSION_ID and
  c.BTS_BUCKET_ID = g.BTS_BUCKET_ID
  left join
  (
    SELECT
    count(*) AS porder_num,
    DAY_START,
    BTS_PLAN_ID,
    BTS_VERSION_ID,
    BTS_BUCKET_ID
    FROM
    BTS_ORDER_REPORT
    WHERE
    glb_plf = 'pc'
    -- AND glb_b = 'c'
    AND glb_fmd = 'mr_T_3'
    AND DAY_START = date '2018-08-18'
    AND BTS_PLAN_ID = '13'
    AND (ORDER_STATUS = '1' or ORDER_STATUS = '8')
    group by
    DAY_START, BTS_PLAN_ID, BTS_VERSION_ID, BTS_BUCKET_ID
  ) h
    on
  c.DAY_START = h.DAY_START and c.BTS_PLAN_ID = h.BTS_PLAN_ID and c.BTS_VERSION_ID = h.BTS_VERSION_ID and
  c.BTS_BUCKET_ID = h.BTS_BUCKET_ID

  left join
  (
    SELECT
    sum(ORDER_AMOUNT) AS amount,
    DAY_START,
    BTS_PLAN_ID,
    BTS_VERSION_ID,
    BTS_BUCKET_ID
    FROM
    BTS_ORDER_REPORT
    WHERE
    glb_plf = 'pc'
    -- AND glb_b = 'c'
    AND glb_fmd = 'mr_T_3'
    AND DAY_START = date '2018-08-18'
    AND BTS_PLAN_ID = '13'
    AND (ORDER_STATUS = '1' or ORDER_STATUS = '8')
    group by
    DAY_START, BTS_PLAN_ID, BTS_VERSION_ID, BTS_BUCKET_ID
  ) i
    on
  c.DAY_START = i.DAY_START and c.BTS_PLAN_ID = i.BTS_PLAN_ID and c.BTS_VERSION_ID = i.BTS_VERSION_ID and
  c.BTS_BUCKET_ID = i.BTS_BUCKET_ID
