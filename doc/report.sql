select
a.pv,
b.uv,
c.exp_num,
d.click_num,
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
  AND glb_b='a'
  AND is_pv = 1
  AND DAY_START>='2018-07-27' AND DAY_START<='2018-07-27'
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
    AND glb_b='a'
    AND is_uv = 1
    AND DAY_START>='2018-07-27' AND DAY_START<='2018-07-27'
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
    AND glb_b='a'
    AND glb_mrlc='T_1'
    AND is_exposure = 1
    AND DAY_START>='2018-07-27' AND DAY_START<='2018-07-27'
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
    AND glb_b='a'
    AND glb_mrlc='T_1'
    AND is_click = 1
    AND DAY_START>='2018-07-27' AND DAY_START<='2018-07-27'
    group by
    STAT_GROUP_MINUTES
  ) d
    on
  a.STAT_GROUP_MINUTES = d.STAT_GROUP_MINUTES
order by
a.STAT_GROUP_MINUTES