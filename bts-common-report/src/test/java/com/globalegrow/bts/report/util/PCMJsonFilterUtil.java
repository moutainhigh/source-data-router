package com.globalegrow.bts.report.util;

import com.globalegrow.report.JsonLogFilter;

public class PCMJsonFilterUtil {


    public static JsonLogFilter tie() {
        JsonLogFilter tie = new JsonLogFilter();
        tie.setJsonPath("$.glb_t");
        tie.setValueFilter("ie");
        return tie;
    }

    public static JsonLogFilter tic() {
        JsonLogFilter tic = new JsonLogFilter();
        tic.setJsonPath("$.glb_t");
        tic.setValueFilter("ic");
        return tic;
    }

    public static JsonLogFilter ubcNotNull() {
        JsonLogFilter ubcNotNull = new JsonLogFilter();
        ubcNotNull.setJsonPath("$.glb_ubcta");
        ubcNotNull.setFilterRule("not_null");
        return ubcNotNull;
    }
    // contains_backward
    public static JsonLogFilter glb_p_Category(){
        JsonLogFilter jsonLogFilter = new JsonLogFilter();
        jsonLogFilter.setJsonPath("$.glb_p");
        jsonLogFilter.setFilterRule("contains_backward");
        jsonLogFilter.setValueFilter("category-");
        return jsonLogFilter;
    }


    public static JsonLogFilter glb_p_CategoryCart(){
        JsonLogFilter jsonLogFilter = new JsonLogFilter();
        jsonLogFilter.setJsonPath("$.glb_p");
        jsonLogFilter.setFilterRule("contains_backward");
        jsonLogFilter.setValueFilter("p-");
        return jsonLogFilter;
    }

    // glb_ubcta category
    public static JsonLogFilter glb_ubcta_p_Category() {
        JsonLogFilter jsonLogFilter = new JsonLogFilter();
        jsonLogFilter.setJsonPath("$.glb_ubcta.p");
        jsonLogFilter.setFilterRule("contains_backward");
        jsonLogFilter.setValueFilter("category-");
        return jsonLogFilter;
    }





    public static JsonLogFilter glb_bB() {
        JsonLogFilter tic = new JsonLogFilter();
        tic.setJsonPath("$.glb_b");
        tic.setValueFilter("b");
        return tic;
    }

    public static JsonLogFilter glb_bC() {
        JsonLogFilter tic = new JsonLogFilter();
        tic.setJsonPath("$.glb_b");
        tic.setValueFilter("c");
        return tic;
    }

    public static JsonLogFilter glb_pmMp() {
        JsonLogFilter glb_pm = new JsonLogFilter();
        glb_pm.setJsonPath("$.glb_pm");
        glb_pm.setValueFilter("mp");
        return glb_pm;
    }

    public static JsonLogFilter glb_filterRec() {
        JsonLogFilter glb_filterRec = new JsonLogFilter();
        glb_filterRec.setJsonPath("$.glb_filter.sort");
        glb_filterRec.setFilterRule("contains_or_equal");
        glb_filterRec.setValueFilter("Recommend%recommend&&Recommend");
        return glb_filterRec;
    }

    public static JsonLogFilter glb_ubctaRec() {
        JsonLogFilter glb_filterRec = new JsonLogFilter();
        glb_filterRec.setJsonPath("$.glb_ubcta.sort");
        glb_filterRec.setFilterRule("contains_or_equal");
        glb_filterRec.setValueFilter("Recommend%recommend&&Recommend");
        return glb_filterRec;
    }

    /**
     * 加入购物车 glb_x: ADT
     * @return
     */
    public static JsonLogFilter glb_xADT(){
        JsonLogFilter glb_pm = new JsonLogFilter();
        glb_pm.setJsonPath("$.glb_x");
        glb_pm.setValueFilter("ADT");
        return glb_pm;
    }

    /**
     * 收藏事件
     * @return
     */
    public static JsonLogFilter glb_xADF(){
        JsonLogFilter glb_pm = new JsonLogFilter();
        glb_pm.setJsonPath("$.glb_x");
        glb_pm.setValueFilter("ADF");
        return glb_pm;
    }

    /**
     * glb_x: sku glb_x: addtobag
     * 商品点击事件
     * @return
     */
    public static JsonLogFilter skuClick() {
        JsonLogFilter glb_filterRec = new JsonLogFilter();
        glb_filterRec.setJsonPath("$.glb_x");
        glb_filterRec.setFilterRule("contains_or_equal");
        glb_filterRec.setValueFilter("addtobag%sku&&addtobag");
        return glb_filterRec;
    }

    public static JsonLogFilter glb_ubcta_fmd_mp() {
        JsonLogFilter glb_pm = new JsonLogFilter();
        glb_pm.setJsonPath("$.glb_ubcta.fmd");
        glb_pm.setValueFilter("mp");
        return glb_pm;
    }

    public static JsonLogFilter glb_pmNull() {
        JsonLogFilter jsonLogFilter = new JsonLogFilter();
        jsonLogFilter.setJsonPath("$.glb_pm");
        jsonLogFilter.setFilterRule("null");
        return jsonLogFilter;
    }

    public static JsonLogFilter glb_skuinfoNotnull() {
        JsonLogFilter ubcNotNull = new JsonLogFilter();
        ubcNotNull.setJsonPath("$.glb_skuinfo");
        ubcNotNull.setFilterRule("not_null");
        return ubcNotNull;
    }

}
