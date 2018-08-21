select
a.pv,
b.uv,
c.exp_num,
d.click_num,
e.cart_num,
f.collect_num,
g.order_num,
h.purchase_num,
h.pay_amount,
a.STAT_GROUP_MINUTES
from
(
  SELECT
  count(*) as pv,
  STAT_GROUP_MINUTES
  FROM
  STREAM_RECOMMEND_PV_REPORT
  WHERE
  glb_plf='pc'
  AND glb_b='c'
  AND is_pv = 1
  AND DAY_START>='2018-08-14' AND DAY_START<='2018-08-14'
  group by
  STAT_GROUP_MINUTES
) a
  left join
  (
    SELECT
    count(distinct(GLB_OD)) as uv,
    STAT_GROUP_MINUTES
    FROM
    STREAM_RECOMMEND_UV_REPORT
    WHERE
    glb_plf='pc'
    AND glb_b='c'
    AND is_uv = 1
    AND DAY_START>='2018-08-14' AND DAY_START<='2018-08-14'
    group by
    STAT_GROUP_MINUTES
  ) b
    on
  a.STAT_GROUP_MINUTES = b.STAT_GROUP_MINUTES
  left join
  (
    SELECT
    sum(EXPOSURE_COUNT) AS exp_num,
    STAT_GROUP_MINUTES
    FROM
    STREAM_RECOMMEND_EXP_REPORT
    WHERE
    glb_plf='pc'
    AND glb_b='c'
    AND glb_mrlc='T_3'
    AND is_exposure = 1
    AND DAY_START>='2018-08-11' AND DAY_START<='2018-08-11'
    group by
    STAT_GROUP_MINUTES
  ) c
    on
  a.STAT_GROUP_MINUTES = c.STAT_GROUP_MINUTES
  left join
  (
    SELECT
    count(*) AS click_num,
    STAT_GROUP_MINUTES
    FROM
    STREAM_RECOMMEND_CLICK_REPORT
    WHERE
    glb_plf='pc'
    AND glb_b='c'
    AND glb_mrlc='T_3'
    AND is_click = 1
    AND DAY_START>='2018-08-14' AND DAY_START<='2018-08-14'
    group by
    STAT_GROUP_MINUTES
  ) d
    on
  a.STAT_GROUP_MINUTES = d.STAT_GROUP_MINUTES
  left join
  (
    SELECT
    count(*) AS cart_num,
    STAT_GROUP_MINUTES
    FROM
    STREAM_RECOMMEND_CART_REPORT
    WHERE
    glb_plf='pc'
    AND glb_fmd='mr_T_3'
    AND is_cart= 1
    AND DAY_START>='2018-08-14' AND DAY_START<='2018-08-14'
    group by
    STAT_GROUP_MINUTES
  ) e
    on
  a.STAT_GROUP_MINUTES = e.STAT_GROUP_MINUTES
  left join
  (
    SELECT
    count(*) AS collect_num,
    STAT_GROUP_MINUTES
    FROM
    STREAM_RECOMMEND_COLLECT_REPORT
    WHERE
    glb_plf='pc'
    AND glb_fmd='mr_T_3'
    AND is_collect = 1
    AND DAY_START>='2018-08-14' AND DAY_START<='2018-08-14'
    group by
    STAT_GROUP_MINUTES
  ) f
    on
  a.STAT_GROUP_MINUTES = f.STAT_GROUP_MINUTES
  left join
  (
    SELECT
    count(*) AS order_num,
    STAT_GROUP_MINUTES
    FROM
    STREAM_RECOMMEND_ORDER_REPORT
    WHERE
    glb_plf='pc'
    AND glb_fmd='mr_T_3'
    AND DAY_START>='2018-08-14' AND DAY_START<='2018-08-14'
    AND order_status ='0'
    group by
    STAT_GROUP_MINUTES
  ) g
    on
  a.STAT_GROUP_MINUTES = g.STAT_GROUP_MINUTES
  left join
  (
    SELECT
    count(*) AS purchase_num,
    sum(order_amount)/100  AS pay_amount,
    STAT_GROUP_MINUTES
    FROM
    STREAM_RECOMMEND_ORDER_REPORT
    WHERE
    glb_plf='pc'
    AND glb_fmd='mr_T_3'
    AND DAY_START>='2018-08-14' AND DAY_START<='2018-08-14'
    AND order_status in('1','8')
    group by
    STAT_GROUP_MINUTES
  ) h
    on
  a.STAT_GROUP_MINUTES = h.STAT_GROUP_MINUTES
order by
a.STAT_GROUP_MINUTES