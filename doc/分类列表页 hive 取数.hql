
--曝光
SELECT
  count(*) AS exp_num,
  n.glb_plf,
   n.policy,
   n.add_time
FROM
  (
    SELECT
      log_id,
      get_json_object(glb_ubcta_col, '$.sku') as sku,
      concat_ws('-', year, month, day) as add_time
    FROM
      stg.zf_pc_event_ubcta_info
    WHERE
      concat_ws('-', year, month, day)  BETWEEN '${ADD_TIME_W}'
      AND '${ADD_TIME}'
      AND get_json_object(glb_ubcta_col, '$.sku') <> ''
  ) m
  INNER JOIN (
    SELECT
      log_id,
      glb_plf,
      get_json_object(user_bh_order_seq,'$.policy') as policy,
      concat_ws('-', year, month, day) as add_time
    FROM
      stg.zf_pc_event_info
    WHERE
      concat_ws('-', year, month, day)  BETWEEN '${ADD_TIME_W}'
      AND '${ADD_TIME}'
      and glb_t = 'ie'
      and glb_s = 'b01'
      and glb_pm = 'mp'
      AND glb_ubcta <> ''
      AND  get_json_object(glb_filter,'$.sort') = 'Recommend'
  ) n ON m.log_id = n.log_id and m.add_time = n.add_time
group by
  n.glb_plf,
  n.policy,
  n.add_time
  ;

--点击
SELECT
  count(*) AS click_num,
  glb_plf,
  get_json_object(user_bh_order_seq,'$.policy') as policy,
  concat_ws('-', year, month, day) as add_time
FROM
  stg.zf_pc_event_info
WHERE
  concat_ws('-', year, month, day) BETWEEN '${ADD_TIME_W}'
  AND '${ADD_TIME}'
  AND glb_t = 'ic'
  and glb_s = 'b01'
  and glb_pm = 'mp'
  AND glb_x in ('sku','addtobag')
  AND  get_json_object(glb_filter,'$.sort') = 'Recommend'
group by
  glb_plf,
  get_json_object(user_bh_order_seq,'$.policy'),
  concat_ws('-', year, month, day)
;


--加购
SELECT SUM(m.pam) as cart_num,
m.glb_plf,
m.policy,
m.add_time
FROM
(
    SELECT
        get_json_object(glb_skuinfo, '$.pam') as pam,
        glb_plf,
        get_json_object(glb_skuinfo, '$.sku') as sku,
        get_json_object(user_bh_order_seq,'$.policy') as policy,
         concat_ws('-', year, month, day) as add_time
    FROM
        stg.zf_pc_event_info
    WHERE
        concat_ws('-', year, month, day)  BETWEEN '${ADD_TIME_W}'
        AND '${ADD_TIME}'
        AND glb_t = 'ic'
        AND glb_x = 'ADT'
        and get_json_object(glb_ubcta, '$.fmd')='mp'
        and get_json_object(glb_ubcta, '$.sckw') is null
        -- AND  get_json_object(glb_filter,'$.sort') = 'Recommend'
) m
JOIN stg.zaful_eload_goods n ON m.sku=n.goods_sn
group by
  m.glb_plf,m.policy,m.add_time
;

-- 订单&金额
select sum(b.goods_number) as seals_volumn,sum(b.pay_amount) as amount,count(*) as order_num,a.policy,'2018-08-28' as add_time from (
select * from (
  select m.policy,m.sku,m.glb_od,n.glb_u,m.glb_plf
from (
  SELECT
        get_json_object(glb_skuinfo, '$.pam') as pam,
        glb_plf,
        get_json_object(glb_skuinfo, '$.sku') as sku,
        get_json_object(user_bh_order_seq,'$.policy') as policy,
        glb_od,
         concat_ws('-', year, month, day) as add_time
    FROM
        stg.zf_pc_event_info
    WHERE
        concat_ws('-', year, month, day)  BETWEEN '2018-08-21'
        AND '2018-08-28'
        AND glb_t = 'ic'
        AND glb_plf = 'pc'
        AND glb_x = 'ADT'
        and get_json_object(glb_ubcta, '$.fmd')='mp'
        and get_json_object(glb_ubcta, '$.sckw') is null
       )m
       JOIN dw_zaful_recommend.zaful_od_u_map n ON m.glb_od = n.glb_od)p
       where p.policy is not null)a
       join (
         SELECT
  p.goods_sn,
  x.user_id,
  x.order_status,
  p.goods_price * p.goods_number as pay_amount,
  p.goods_number
FROM
  (
    SELECT
      order_id,
      user_id,
      add_time,
      order_status
    FROM
      stg.zaful_eload_order_info
    WHERE
      from_unixtime(add_time, 'yyyy-MM-dd') = '2018-08-28' and order_status not in ('0','11')
  ) x
JOIN stg.zaful_eload_order_goods p ON x.order_id = p.order_id
         )b on a.glb_u = b.user_id and a.sku = b.goods_sn group by a.policy