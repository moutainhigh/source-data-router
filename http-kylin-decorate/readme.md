# kylin 对外服务接口封装
## 请求参数示例
### 报表查询（均值）
{"planId":13,"type":"query","productLineCode":"ZF", "groupByFields":["day_start","bts_planid","bts_versionid"],"whereFields":{"bts_planid":"13","day_start":"2018-08-10"},"betweenFields":{},"orderFields":{},"startPage":1,"pageSize":10}
### 报表查询（导出）

## 报表查询（全部）

### 报表配置查询

## 缓存清理
172.31.44.72<br>
curl "http://localhost:38093/report-config/bts-kylin?planId=45&productLineCode=ZF&queryType=query" <br>
curl "http://localhost:38093/report-config/bts-field?planId=45&productLineCode=ZF" <br>

## EMP 接口
请求接口地址：
[接口地址](http://ems.appinthestore.com.ems_auto_marketing.php5.egomsl.com/marketing/api-bts-email-info/get-email-order-info?module_name=marketing_email&plan_id=1&data_flag=3)
请求参数：
    plan_id:实验id <br>
    data_flag:四种数据格式 1：分组条件实验id，查询条件实验id，数据一条 2：分组条件实验id、版本id， 查询条件实验id，多条数据 3： 分组条件实验id、版本id、邮件发送日期， 查询条件实验id，多条数据 4：分组条件实验id、版本id、邮件发送日期， 查询条件实验id，发送日期， 多条数据
    <br>day: 发送日期<br>
返回数据：
```json
{
	"data": [{
	    "total_count": 35436,// 邮件发送数量
		"send_ok_count": 10000, // 送达用户量
		"open_count": 2000, // 打开用户量
		"click_count": 500, // 点击用户量
		"order_nums": 20, // 生单量
		"order_money": 200, // 订单总金额
		"order_user": 10, // 生单用户数
		"payed_user": 5, // 付款用户 
		"payed_order_nums": 10, // 付款订单数  
		"payed_order_money": 200, // 付款总金额（USD）
		"plan_id": 1, // 实验id
		"version_id": 1, // 版本id
		"day": "2018-09-24" // 日期
	}, {  
		"send_ok_count": 10000,
		"open_count": 2000,
		"click_count": 500,
		"order_nums": 20,
		"order_money": 200,
		"order_user": 10,
		"payed_user": 5,
		"payed_order_nums": 10,
		"payed_order_money": 200,
		"plan_id": 1,
		"version_id": 2,
		"day": "2018-09-25"
	}],
	"status": 0
}
```
