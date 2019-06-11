# zaful app 用户特征接口服务
## 线上部署检查
curl --header "Content-Type: application/json" --request POST --data '{"fb_adset_id":"23843238186800488","target":"algorithm"}' http://172.31.33.169:38194/fb-ad-user-feature
curl --header "Content-Type: application/json" --request POST --data '{"fb_adset_id":"23843238186800488","target":"algorithm"}' http://172.31.44.72:38194/fb-ad-user-feature
curl --header "Content-Type: application/json" --request POST --data '{"fb_adset_id":"23843238186800488","target":"algorithm"}' http://172.31.29.140:38194/fb-ad-user-feature
curl --header "Content-Type: application/json" --request POST --data '{"fb_adset_id":"23843238186800488","target":"algorithm"}' http://172.31.27.225:38194/fb-ad-user-feature
### 线上
curl --header "Content-Type: application/json" --request POST --data '{"fb_adset_id":"23843238186800488","target":"algorithm"}' http://172.31.33.169:38294/fb-ad-user-feature

## 用户分层特征
000：老用户，无回访 <br>
010：老用户，有回访 <br>
100：新用户，无回访 <br>
110：新用户，有回访 <br>
111：游客回访"
