package com.globalegrow.bts.report;

import com.globalegrow.util.JacksonUtil;
import org.junit.Test;

import java.util.Map;

public class OrderTest {

    String s = "{\"glb_osr_referrer\":\"https://www.google.com/\",\"glb_dc\":\"1301\",\"glb_ubcta\":{\"sckw\":\"mini lace \",\"fmd\":\"mp\",\"sort\":\"recommend\"},\"glb_osr_landing\":\"https://www.zaful.com/\",\"glb_x\":\"ADT\",\"glb_cl\":\"https://www.zaful.com/mini-lace-spaghetti-strap-dress-p_518994.html\",\"glb_w\":\"9747\",\"glb_t\":\"ic\",\"glb_u\":\"17629549\",\"glb_skuinfo\":{\"sku\":\"252070303\",\"price\":\"19.86\",\"pam\":\"1\",\"pc\":\"50\",\"zt\":0},\"glb_p\":\"p-518994\",\"glb_od\":\"100131542048379096941216\",\"glb_ksku\":\"252070303\",\"glb_plf\":\"pc\",\"glb_tm\":\"1542161112189\",\"glb_pm\":\"mb\",\"glb_d\":\"10013\",\"glb_pl\":\"https://www.zaful.com/mini-lace-spaghetti-strap-dress-p_518993.html\",\"glb_b\":\"c\",\"glb_oi\":\"llk1lo6rpitoq97au0oksvbft2\",\"timestamp\":\"1542161113000\",\"db_order_info\":{\"order_goods_id\":0,\"goods_num\":1,\"order_id\":8591604,\"user_id\":null,\"sku\":\"252070303\",\"gmv\":0,\"amount_num_price\":1986,\"order_status\":\"0\",\"has_sent\":false,\"order_data\":false}}";


    @Test
    public void test() throws Exception {
        Map<String, Object> objectMap = JacksonUtil.readValue(s, Map.class);
        objectMap.entrySet().stream().forEach(e -> {
            System.out.println(String.valueOf(e.getValue()));
        });
    }

}
