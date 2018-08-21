package com.globalegrow.test.data.generate.kafka;

import com.alibaba.fastjson.JSONObject;
import com.globalegrow.mapper.bts.BtsResultMapper;
import com.globalegrow.mapper.zaful.OrderGoodsMapper;
import com.globalegrow.mapper.zaful.OrderInfoMapper;
import com.globalegrow.model.bts.BtsResult;
import com.globalegrow.model.zaful.OrderGoodsWithBLOBs;
import com.globalegrow.model.zaful.OrderInfo;
import com.globalegrow.model.zaful.OrderInfoWithBLOBs;
import com.globalegrow.test.data.generate.service.IBuryPointService;
import com.globalegrow.test.data.generate.utils.SpringContextUtils;
import com.globalegrow.util.JacksonUtil;
import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

@Component
//@ConfigurationProperties("extdata")
public class BuryPointLogHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    Map<String, Object> props = new HashMap(){{
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.18.0.2:9092");
        put(ProducerConfig.RETRIES_CONFIG, 0);
        put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        put(ProducerConfig.LINGER_MS_CONFIG, 1);
        put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    }};

    ProducerFactory<String, String> factory = new DefaultKafkaProducerFactory(props);
    KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(factory);

    //@Autowired
    //private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private IBuryPointService buryPointService;
    // @Value("${kafka.default.topic}")
    private String defaultTopic = "kylin_bts_zaful_log_1";

    /**
     * 从配置文件里面定义的数组extdata.invokeinfo获取值
     */
    private List<String> invokeinfo = new ArrayList<String>();

    @Autowired
    private BtsResultMapper btsResultMapper;

    @Autowired
    private OrderGoodsMapper orderGoodsMapper;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    List<Long> plans = Arrays.asList(new Long[]{1L, 2L});

    List<Long> versionIds = Arrays.asList(new Long[]{3L, 4L});



    /**
     * 监听源数据kafka主题,有消息就读取
     *
     * @throws Exception
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    @KafkaListener(topics = {"dy_bts_topic_1"})
    public void processLog(String logLine) throws Exception {
        Thread.sleep(1000);
        try {
            // 解析埋点，已json格式放到大禹项目的kafka
            if (StringUtils.isNoneBlank(logLine)) {
                //HashMap<String, String> logMap = this.buryPointService.logToMap(logLine);
                Map<String, String> logMap = JacksonUtil.readValue(logLine, Map.class);
                if (MapUtils.isNotEmpty(logMap)) {

                    String cookie = logMap.get("glb_od");
                    if (StringUtils.isNotBlank(cookie)) {

                        BtsResult btsResult = new BtsResult();

                        HashFunction hf = Hashing.murmur3_128();
                        int hash =  hf.hashString(cookie, Charsets.UTF_8).asInt();
                        if (hash < 0) {
                            hash = Math.abs(hash);
                        }

                        btsResult.setCookie(cookie);
                        btsResult.setPlanId(plans.get(hash % 2));
                        btsResult.setPlanName("test");
                        btsResult.setVersionId(versionIds.get(hash % 2));
                        btsResult.setVersionName("test");
                        btsResult.setPlanCode("test");
                        btsResult.setBucketId((byte) 1);
                        btsResult.setProductLineCode("test");
                        btsResult.setRecormandType("test");
                        btsResult.setPlanStatus((byte) 1);
                        btsResult.setCreator(1L);
                        btsResult.setVersionFlag("1");
                        this.btsResultMapper.insertSelective(btsResult);
                        logMap.put("versionid", btsResult.getVersionId() + "");
                        logMap.put("planid", btsResult.getPlanId() + "");
                        logMap.put("bucketid", btsResult.getBucketId() + "");
                        if ("ic".equals(logMap.get("glb_t")) && "ADT".equals(logMap.get("glb_x")) && !"".equals(logMap.get("glb_ubcta")) && !"".equals(logMap.get("glb_skuinfo"))
                                && StringUtils.isNotEmpty(logMap.get("glb_u"))) {

                            if (hash % 3 == 0) {
                                OrderInfoWithBLOBs orderInfo = new OrderInfoWithBLOBs();
                                orderInfo.setUserId(Integer.valueOf(logMap.get("glb_u")));
                                orderInfo.setRealpay(new BigDecimal(10));
                                if (hash % 5 == 0) {
                                    orderInfo.setOrderStatus(true);
                                }
                                this.orderInfoMapper.insertSelective(orderInfo);

                                OrderGoodsWithBLOBs orderGoods = new OrderGoodsWithBLOBs();
                                orderGoods.setOrderId(orderInfo.getOrderId());
                                String tm = logMap.get("glb_tm");
                                if (tm.length() > 11) {
                                    tm = tm.substring(0, 10);
                                }
                                orderGoods.setAddtime(Integer.valueOf(tm));
                                Map<String, String> skuInfo = JacksonUtil.readValue(logMap.get("glb_skuinfo"), Map.class);
                                orderGoods.setGoodsSn(skuInfo.get("sku"));
                                orderGoods.setGoodsNumber((short) 1);
                                orderGoods.setGoodsPrice(new BigDecimal(1));
                                this.orderGoodsMapper.insertSelective(orderGoods);
                            }

                        }

                        Thread.sleep(1000);

                        this.sendToKafka(logMap, this.defaultTopic);

                    }


                }
            }
        } catch (Exception e) {
            logger.error("BuryPointLogHandler.processLog exception", e);
        }

    }

    /**
     * 根据配置调用扩展字段方法并发送kafka
     *
     * @param logMap
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void invokeExtMethodAndSendToKafka(HashMap<String, String> logMap)
            throws IllegalAccessException, InvocationTargetException {
        for (String str : invokeinfo) {
            @SuppressWarnings("unchecked")
            Map<String, String> sourceMap = (Map<String, String>) logMap.clone();
            String[] invokeInfos = str.split(":");
            Object serviceObj = SpringContextUtils.getBean(invokeInfos[0]);

            Method[] methods = serviceObj.getClass().getMethods();
            if (ArrayUtils.isNotEmpty(methods)) {
                Method invokeMethod = this.findMethod(invokeInfos[1], methods);
                invokeMethod.invoke(serviceObj, sourceMap);
                this.sendToKafka(sourceMap, invokeInfos[2]);
            }
        }
    }

    private void sendToKafka(Map<String, String> logMap, String topic) {
        String jsonLog = JSONObject.toJSONString(logMap);
        // logger.info("send to kylin kafka：{}", jsonLog);
        kafkaTemplate.send(topic, jsonLog);
        // send kafka异常 讲信息保存到数据库 待定
    }

    /**
     * 找到被调用的方法
     *
     * @param func
     * @param methods
     * @return
     */
    private Method findMethod(String func, Method[] methods) {
        Method invokeMethod = null;
        for (Method method : methods) {
            if (func.equals(method.getName())) {
                invokeMethod = method;
                break;
            }
        }
        return invokeMethod;
    }

    public List<String> getInvokeinfo() {
        return invokeinfo;
    }

    public void setInvokeinfo(List<String> invokeinfo) {
        this.invokeinfo = invokeinfo;
    }

}
