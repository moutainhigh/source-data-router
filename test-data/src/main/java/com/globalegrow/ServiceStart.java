package com.globalegrow;

import java.util.*;
import com.globalegrow.util.JacksonUtil;
import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@EnableAspectJAutoProxy
@SpringBootApplication(scanBasePackages = {"com.globalegrow"})
@EnableScheduling
@EnableKafka
@RestController
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class,DataSourceTransactionManagerAutoConfiguration.class, MybatisAutoConfiguration.class})
public class ServiceStart {

    @RequestMapping("/")
    public java.lang.String index() {
        return "";
    }


    public static Map<String, String> EMPTY_ORDER = new HashMap() {{
        put("order_id", "_skip");
        put("order_status", "0");
        put("pay_total", "0");
        put("paid_order", "_skip");
    }};

    public static Map<String, String> EMPTY_BTS = new HashMap() {{
        put("bucket_id", "0");
        put("version_id", "0");
        put("plan_id", "0");
    }};

    public static Map<String, List<String>> PLANS = new HashMap() {{
        put("7_8", Arrays.asList(new java.lang.String[]{"5"}));
        put("7_9", Arrays.asList(new java.lang.String[]{"1","2","3"}));
        put("7_10", Arrays.asList(new java.lang.String[]{"4"}));
        put("7_11", Arrays.asList(new java.lang.String[]{"6"}));
        put("7_12", Arrays.asList(new java.lang.String[]{"7"}));
        put("7_13", Arrays.asList(new java.lang.String[]{"8"}));
        put("7_14", Arrays.asList(new java.lang.String[]{"9"}));
        put("7_15", Arrays.asList(new java.lang.String[]{"10"}));
        put("8_16", Arrays.asList(new java.lang.String[]{"1","2","3","4","5","6","7","8","9","10"}));
    }};

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ServiceStart.class, args);

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

        generateTestData(kafkaTemplate);

    }



    private static void generateTestData(KafkaTemplate<String, String> kafkaTemplate) throws Exception {
        String source = "{\"bts\":{\"bucket_id\":\"1\",\"version_id\":\"3\",\"plan_id\":\"1\"},\"uv\":\"1\",\"glb_ubcta\":\"_skip\",\"pv\":\"1\",\"exposure_count\":\"0\",\"add_cart_num\":\"0\",\"glb_x\":\"ADT\",\"zaful_order\":{\"order_status\":\"1\",\"paid_order\":\"1\",\"pay_total\":\"1\",\"order_id\":\"1\"},\"click_num\":\"0\",\"glb_t\":\"ic\",\"glb_u\":\"2345465\",\"glb_skuinfo\":\"_skip\",\"mrlc\":\"_skip\",\"glb_od\":\"1001315292910982208rbfud82690258\",\"glb_plf\":\"pc\",\"glb_tm\":\"1531742468428\",\"glb_pm\":\"mp\",\"sku\":\"_skip\",\"fmd\":\"mr_T_1\",\"glb_b\":\"b\"}";
        Map<String, Object> map = JacksonUtil.readValue(source, Map.class);

        PLANS.entrySet().forEach(e -> {


            String planid = e.getKey().split("_")[0];
            String versionid = e.getKey().split("_")[1];
            // String bucketid = "5";

            e.getValue().forEach(b -> {

                Map<String, String> bts = EMPTY_BTS;
                bts.put("bucket_id", b);
                bts.put("version_id", versionid);
                bts.put("plan_id", planid);

                map.put("bts", bts);

                // Date date = new Date();
                Calendar calendar = Calendar.getInstance();

                for (int i = 1; i < 10; i++) {
                    calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
                    map.put("glb_tm", calendar.getTimeInMillis() + "");
                    //System.out.println(DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss"));
                    for(int j = 0; j<2 ; j++){
                        String cookie = UUID.randomUUID().toString();

                        HashFunction hf = Hashing.murmur3_128();
                        int hash =  hf.hashString(cookie, Charsets.UTF_8).asInt();
                        if (hash < 0) {
                            hash = Math.abs(hash);
                        }
                        map.put("glb_od", cookie);
                        if (hash % 3 == 0) {
                            map.put("glb_t", "ic");
                            if (hash % 2 ==0) {
                                map.put("click_num", "1");
                                map.put("add_cart_num", "1");
                                if (hash % 5 == 0) {
                                    String orderId = UUID.randomUUID().toString();
                                    Map<String, String> order = EMPTY_ORDER;
                                    order.put("order_status", "1");
                                    order.put("order_id", orderId);
                                    order.put("paid_order", orderId);
                                    order.put("pay_total", "20");
                                    map.put("zaful_order", order);
                                }else {
                                    String orderId = UUID.randomUUID().toString();
                                    Map<String, String> order = EMPTY_ORDER;
                                    order.put("order_status", "0");
                                    order.put("order_id", orderId);
                                    order.put("paid_order", "_skip");
                                    order.put("pay_total", "0");
                                    map.put("zaful_order", order);
                                }
                            }else {
                                map.put("click_num", "1");
                            }
                        }else {
                            map.put("glb_t", "ie");
                            if (hash % 2 == 0) {
                                map.put("exposure_count", "1");
                            }
                        }

                        try {
                            System.out.println("生成测试数据");
                            kafkaTemplate.send("kylin_bts_zaful_report_test_15", JacksonUtil.toJSon(map));
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                    }

                }


            });



        });


    }

}
