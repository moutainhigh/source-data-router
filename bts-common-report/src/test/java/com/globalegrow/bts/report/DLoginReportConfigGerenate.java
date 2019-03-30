package com.globalegrow.bts.report;

import com.globalegrow.report.JsonLogFilter;
import com.globalegrow.report.ReportBuildRule;
import com.globalegrow.report.ReportKafkaConfig;
import com.globalegrow.report.ReportQuotaFieldConfig;
import com.globalegrow.util.DyBeanUtils;
import com.globalegrow.util.JacksonUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName DLoginReportConfigGerenate
 * @Description D网 PC 快速登录AB测试报表取数指标
 * @Author tangliuyi
 * @Date 2018/11/5 10:35
 * @Version 1.0
 */
public class DLoginReportConfigGerenate {

    private Map<String, String> bts = new HashMap() {

        private static final long serialVersionUID = 3456809019097454769L;

        {
            put("planid", "_skip");
            put("versionid", "_skip");
            put("bucketid", "_skip");
        }
    };

    @Test
    public void stringTest() {
        String s = "$.skuInfo.wrwer";
        String s1 = s.substring(s.indexOf("$.") + 2);
        if (s1.contains(".")) {
            s1 = s1.substring(0, s1.indexOf("."));
            System.out.println(s1);
        }
        System.out.println();
    }

    @Test
    public void reportJson() throws Exception {
        SearchRecommendReportQuotaModel searchRecommendReportQuotaModel = new SearchRecommendReportQuotaModel();
        searchRecommendReportQuotaModel.setTimestamp(System.currentTimeMillis());
        System.out.println(JacksonUtil.toJSon(searchRecommendReportQuotaModel));
    }


    @Test
    public void generate() throws Exception {
        ReportBuildRule rule = new ReportBuildRule();
        rule.setGlobaleFilter(true);

        List<JsonLogFilter> globaleFilters = new ArrayList<>();

        JsonLogFilter btsFilter = new JsonLogFilter();
        btsFilter.setJsonPath("$.glb_bts");
        btsFilter.setFilterRule("not_null");
        globaleFilters.add(btsFilter);

        JsonLogFilter btsValuePlanIdNotNullFilter = new JsonLogFilter();
        btsValuePlanIdNotNullFilter.setJsonPath("$.glb_bts.planid");
        btsValuePlanIdNotNullFilter.setFilterRule("not_null");
        globaleFilters.add(btsValuePlanIdNotNullFilter);

        JsonLogFilter btsValueversionidNotNullFilter = new JsonLogFilter();
        btsValueversionidNotNullFilter.setJsonPath("$.glb_bts.versionid");
        btsValueversionidNotNullFilter.setFilterRule("not_null");
        globaleFilters.add(btsValueversionidNotNullFilter);

        //终端
//        JsonLogFilter btsPlatformNotNullFilter = new JsonLogFilter();
//        btsPlatformNotNullFilter.setJsonPath("$.glb_plf");
//        btsPlatformNotNullFilter.setFilterRule("not_null");
//        globaleFilters.add(btsPlatformNotNullFilter);

//        JsonLogFilter btsValuebucketidNotNullFilter = new JsonLogFilter();
//        btsValuebucketidNotNullFilter.setJsonPath("$.glb_bts.bucketid");
//        btsValuebucketidNotNullFilter.setFilterRule("not_null");
//        globaleFilters.add(btsValuebucketidNotNullFilter);

        JsonLogFilter siteFilter = new JsonLogFilter();
        siteFilter.setJsonPath("$.glb_d");
        siteFilter.setValueFilter("10009");
        globaleFilters.add(siteFilter);

        rule.setGlobaleJsonFilters(globaleFilters);

        rule.setReportName("BTS_DL_QUICK_REGISTER_LOGIN");
        rule.setDescription("D网 PC+M+IPAD 快速登录AB测试报表取数指标配置");
        DQuickLoginReportQuotaModel quickLoginReportQuotaModel=new DQuickLoginReportQuotaModel();

        rule.setReportDefaultValues(DyBeanUtils.objToMap(quickLoginReportQuotaModel));


        ReportKafkaConfig reportKafkaConfig = new ReportKafkaConfig();
        reportKafkaConfig.setBootstrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setDataSourceTopic("glbg-analitic");//埋点上报的topic
        reportKafkaConfig.setBootstrapGroupId("dy_bts_d_quick_login_report");
        reportKafkaConfig.setReportStrapServers("172.31.35.194:9092,172.31.50.250:9092,172.31.63.112:9092");
        reportKafkaConfig.setReportDataTopic("dy_bts_d_quick_login_report");

        rule.setReportFromKafka(reportKafkaConfig);


        List<ReportQuotaFieldConfig> reportQuotaFieldConfigs = new ArrayList<>();

        quota(reportQuotaFieldConfigs);

        rule.setReportQuotaFieldConfigs(reportQuotaFieldConfigs);

        System.out.println(JacksonUtil.toJSon(rule));
    }

    private void quota(List<ReportQuotaFieldConfig> reportQuotaFieldConfigs) {
        // 样本量
        ReportQuotaFieldConfig specimen = new ReportQuotaFieldConfig();
        specimen.setQuotaFieldName("specimen");
        specimen.setDefaultValue("_skip");
        specimen.setExtractValueJsonPath("$.glb_od");
        specimen.setValueEnum("quotaStringValueExtractFromLog");
        // 过滤条件
        List<JsonLogFilter> specimenFilter = new ArrayList<>();
//        JsonLogFilter plf = new JsonLogFilter();
//        plf.setJsonPath("$.glb_plf");
//        plf.setValueFilter("pc");
//        specimenFilter.add(plf);

//        JsonLogFilter tie = new JsonLogFilter();
//        tie.setJsonPath("$.glb_t");
//        tie.setValueFilter("ie");
//        specimenFilter.add(tie);

//        JsonLogFilter glbs = new JsonLogFilter();
//        glbs.setJsonPath("$.glb_s");
//        glbs.setValueFilter("b02");
//        specimenFilter.add(glbs);

//        JsonLogFilter ubc = new JsonLogFilter();
//        ubc.setJsonPath("$.glb_ubcta");
//        ubc.setFilterRule("null");
//        specimenFilter.add(ubc);
        specimen.setJsonLogFilters(specimenFilter);

        reportQuotaFieldConfigs.add(specimen);

        // 购物车
        ReportQuotaFieldConfig shoppingCarUv = new ReportQuotaFieldConfig();
        shoppingCarUv.setQuotaFieldName("shopping_car_uv");
        shoppingCarUv.setDefaultValue("_skip");
        shoppingCarUv.setExtractValueJsonPath("$.glb_od");
        shoppingCarUv.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件 UV glb_t=ie，glb_s=d01计算glb_od的去重后的数量
        List<JsonLogFilter> carUvFilter = new ArrayList<>();
        JsonLogFilter glbType = new JsonLogFilter();
        glbType.setJsonPath("$.glb_t");
        glbType.setValueFilter("ie");
        carUvFilter.add(glbType);

        JsonLogFilter pageSubType = new JsonLogFilter();
        pageSubType.setJsonPath("$.glb_s");
        pageSubType.setValueFilter("d01");
        carUvFilter.add(pageSubType);

//        JsonLogFilter platform = new JsonLogFilter();
//        platform.setJsonPath("$.glb_plf");
//        platform.setValueFilter("pc");
//        carUvFilter.add(platform);

        shoppingCarUv.setJsonLogFilters(carUvFilter);
        reportQuotaFieldConfigs.add(shoppingCarUv);

        // 购物车 PV
        ReportQuotaFieldConfig shoppingCarPv = new ReportQuotaFieldConfig();
        shoppingCarPv.setQuotaFieldName("shopping_car_pv");
        shoppingCarPv.setDefaultValue(0);
        shoppingCarPv.setExtractValueJsonPath("$.glb_od");
        shoppingCarPv.setValueEnum("countOneWithFilter");
        //过滤条件 glb_t=ie，glb_s=d01计算glb_od的数量
        shoppingCarPv.setJsonLogFilters(carUvFilter);
        reportQuotaFieldConfigs.add(shoppingCarPv);

        // check out点击PV
        ReportQuotaFieldConfig checkOutPv = new ReportQuotaFieldConfig();
        checkOutPv.setQuotaFieldName("check_out_pv");
        checkOutPv.setDefaultValue(0);
        checkOutPv.setExtractValueJsonPath("$.glb_od");
        checkOutPv.setValueEnum("countOneWithFilter");
        //过滤条件 glb_t=ic，glb_x=checkout,glb_s=d01,glb_plf=pc,计算glb_od的数量
        List<JsonLogFilter> checkOutPvFilter = new ArrayList<>();
        JsonLogFilter glbTypeIc = new JsonLogFilter();
        glbTypeIc.setJsonPath("$.glb_t");
        glbTypeIc.setValueFilter("ic");

        JsonLogFilter eventInfo = new JsonLogFilter();
        eventInfo.setJsonPath("$.glb_x");
        eventInfo.setValueFilter("checkout");
        checkOutPvFilter.add(glbTypeIc);
        checkOutPvFilter.add(pageSubType);
        checkOutPvFilter.add(eventInfo);

        checkOutPv.setJsonLogFilters(checkOutPvFilter);
        reportQuotaFieldConfigs.add(checkOutPv);

        // check out点击UV
        ReportQuotaFieldConfig checkOutUv = new ReportQuotaFieldConfig();
        checkOutUv.setQuotaFieldName("check_out_uv");
        checkOutUv.setDefaultValue("_skip");
        checkOutUv.setExtractValueJsonPath("$.glb_od");
        checkOutUv.setValueEnum("quotaStringValueExtractFromLog");
        //glb_t=ic，glb_x=checkout,glb_s=d01,glb_plf=pc,计算glb_od的去重后的数量
        checkOutUv.setJsonLogFilters(checkOutPvFilter);
        reportQuotaFieldConfigs.add(checkOutUv);


        // 购物车登录点击PV
        ReportQuotaFieldConfig carLoginPv = new ReportQuotaFieldConfig();
        carLoginPv.setQuotaFieldName("car_login_pv");
        carLoginPv.setDefaultValue(0);
        carLoginPv.setExtractValueJsonPath("$.glb_od");
        carLoginPv.setValueEnum("countOneWithFilter");
        //glb_t=ic，glb_x=SIGNIN_CART,glb_s=d01,glb_plf=pc,计算glb_od的数量
        List<JsonLogFilter> carLoginPvFilter = new ArrayList<>();
        JsonLogFilter subEventInfoCart = new JsonLogFilter();
        subEventInfoCart.setJsonPath("$.glb_x");
        subEventInfoCart.setValueFilter("SIGNIN_CART");
        carLoginPvFilter.add(glbTypeIc);
        carLoginPvFilter.add(subEventInfoCart);
        carLoginPvFilter.add(pageSubType);
        //carLoginPvFilter.add(platform);
        carLoginPv.setJsonLogFilters(carLoginPvFilter);
        reportQuotaFieldConfigs.add(carLoginPv);


       // 购物车登录点击UV
        ReportQuotaFieldConfig carLoginUv = new ReportQuotaFieldConfig();
        carLoginUv.setQuotaFieldName("car_login_uv");
        carLoginUv.setDefaultValue("_skip");
        carLoginUv.setExtractValueJsonPath("$.glb_od");
        carLoginUv.setValueEnum("quotaStringValueExtractFromLog");
        //glb_t=ic，glb_x=SIGNIN_CART,glb_s=d01计算glb_od的去重后的数量
        carLoginUv.setJsonLogFilters(carLoginPvFilter);
        reportQuotaFieldConfigs.add(carLoginUv);

        // 登录点击PV
        ReportQuotaFieldConfig loginPv = new ReportQuotaFieldConfig();
        loginPv.setQuotaFieldName("login_pv");
        loginPv.setDefaultValue(0);
        loginPv.setExtractValueJsonPath("$.glb_od");
        loginPv.setValueEnum("countOneWithFilter");
        //glb_t=ic，glb_x=SIGNIN,glb_s=f01计算glb_od的数量
        List<JsonLogFilter> loginPvFilter = new ArrayList<>();
        loginPvFilter.add(glbTypeIc);
        JsonLogFilter subEventInfo = new JsonLogFilter();
        subEventInfo.setJsonPath("$.glb_x");
        subEventInfo.setValueFilter("SIGNIN");
        loginPvFilter.add(subEventInfo);
        JsonLogFilter pageSubTypeF = new JsonLogFilter();
        pageSubTypeF.setJsonPath("$.glb_s");
        pageSubTypeF.setValueFilter("f01");
        loginPvFilter.add(pageSubTypeF);

        loginPv.setJsonLogFilters(loginPvFilter);
        reportQuotaFieldConfigs.add(loginPv);

       // 登录点击UV
        ReportQuotaFieldConfig loginUv = new ReportQuotaFieldConfig();
        loginUv.setQuotaFieldName("login_uv");
        loginUv.setDefaultValue("_skip");
        loginUv.setExtractValueJsonPath("$.glb_od");
        loginUv.setValueEnum("quotaStringValueExtractFromLog");
        //glb_t=ic，glb_x=SIGNIN,glb_s=f01计算glb_od的去重后的数量

        loginUv.setJsonLogFilters(loginPvFilter);
        reportQuotaFieldConfigs.add(loginUv);

        //购物车注册点击PV
        ReportQuotaFieldConfig carRegisterPv = new ReportQuotaFieldConfig();
        carRegisterPv.setQuotaFieldName("car_register_pv");
        carRegisterPv.setDefaultValue(0);
        carRegisterPv.setExtractValueJsonPath("$.glb_od");
        carRegisterPv.setValueEnum("countOneWithFilter");
        //过滤条件 glb_t=ic，glb_x=REGISTER_CART,glb_s=d01计算glb_od的数量
        List<JsonLogFilter> carRegisterPvFilter = new ArrayList<>();
        carRegisterPvFilter.add(glbTypeIc);
        JsonLogFilter eventRegisterCart = new JsonLogFilter();
        eventRegisterCart.setJsonPath("$.glb_x");
        eventRegisterCart.setValueFilter("REGISTER_CART");
        carRegisterPvFilter.add(eventRegisterCart);
        carRegisterPvFilter.add(pageSubType);
        //carRegisterPvFilter.add(platform);

        carRegisterPv.setJsonLogFilters(carRegisterPvFilter);
        reportQuotaFieldConfigs.add(carRegisterPv);

        //购物车注册点击UV
        ReportQuotaFieldConfig carRegisterUv = new ReportQuotaFieldConfig();
        carRegisterUv.setQuotaFieldName("car_register_uv");
        carRegisterUv.setDefaultValue("_skip");
        carRegisterUv.setExtractValueJsonPath("$.glb_od");
        carRegisterUv.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件glb_t=ic，glb_x=REGISTER_CART,glb_s=d01计算glb_od的去重后的数量
        carRegisterUv.setJsonLogFilters(carRegisterPvFilter);
        reportQuotaFieldConfigs.add(carRegisterUv);

        //注册点击PV
        ReportQuotaFieldConfig registerPv = new ReportQuotaFieldConfig();
        registerPv.setQuotaFieldName("register_pv");
        registerPv.setDefaultValue(0);
        registerPv.setExtractValueJsonPath("$.glb_od");
        registerPv.setValueEnum("countOneWithFilter");
        //过滤条件 glb_t=ic，glb_x=REGISTER,glb_s=f01计算glb_od的数量
        List<JsonLogFilter> registerPvFilter = new ArrayList<>();
        registerPvFilter.add(glbTypeIc);
        JsonLogFilter eventRegister = new JsonLogFilter();
        eventRegister.setJsonPath("$.glb_x");
        eventRegister.setValueFilter("REGISTER");
        registerPvFilter.add(eventRegister);
        registerPvFilter.add(pageSubTypeF);
        //registerPvFilter.add(platform);

        registerPv.setJsonLogFilters(registerPvFilter);
        reportQuotaFieldConfigs.add(registerPv);

        //注册点击UV
        ReportQuotaFieldConfig registerUv = new ReportQuotaFieldConfig();
        registerUv.setQuotaFieldName("register_uv");
        registerUv.setDefaultValue("_skip");
        registerUv.setExtractValueJsonPath("$.glb_od");
        registerUv.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件 glb_t=ic，glb_x=REGISTER,glb_s=f01计算glb_od的去重后的数量
        registerUv.setJsonLogFilters(registerPvFilter);
        reportQuotaFieldConfigs.add(registerUv);

        //购物车fb登录点击PV
        ReportQuotaFieldConfig carFbLoginPv = new ReportQuotaFieldConfig();
        carFbLoginPv.setQuotaFieldName("car_fb_login_pv");
        carFbLoginPv.setDefaultValue(0);
        carFbLoginPv.setExtractValueJsonPath("$.glb_od");
        carFbLoginPv.setValueEnum("countOneWithFilter");
        //过滤条件 glb_t=ic，glb_x=SIGNIN_CART_FB,glb_s=d01计算glb_od的数量
        List<JsonLogFilter> carFbLoginPvFilter = new ArrayList<>();
        carFbLoginPvFilter.add(glbTypeIc);

        JsonLogFilter cartFbEvent = new JsonLogFilter();
        cartFbEvent.setJsonPath("$.glb_x");
        cartFbEvent.setValueFilter("SIGNIN_CART_FB");
        carFbLoginPvFilter.add(cartFbEvent);
        carFbLoginPvFilter.add(pageSubType);
        //carFbLoginPvFilter.add(platform);
        carFbLoginPv.setJsonLogFilters(carFbLoginPvFilter);
        reportQuotaFieldConfigs.add(carFbLoginPv);

        //购物车fb登录点击UV
        ReportQuotaFieldConfig carFbLoginUv = new ReportQuotaFieldConfig();
        carFbLoginUv.setQuotaFieldName("car_fb_login_uv");
        carFbLoginUv.setDefaultValue("_skip");
        carFbLoginUv.setExtractValueJsonPath("$.glb_od");
        carFbLoginUv.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件：glb_t=ic，glb_x=SIGNIN_CART_FB,glb_s=d01计算glb_od的去重后的数量
        carFbLoginUv.setJsonLogFilters(carFbLoginPvFilter);
        reportQuotaFieldConfigs.add(carFbLoginUv);

        //fb登录点击PV
        ReportQuotaFieldConfig fbLoginPv = new ReportQuotaFieldConfig();
        fbLoginPv.setQuotaFieldName("fb_login_click_pv");
        fbLoginPv.setDefaultValue(0);
        fbLoginPv.setExtractValueJsonPath("$.glb_od");
        fbLoginPv.setValueEnum("countOneWithFilter");
        //过滤条件 glb_t=ic，glb_x=SIGNIN_FB,glb_s=f01计算glb_od的数量
        List<JsonLogFilter> fbLoginPvFilter = new ArrayList<>();
        fbLoginPvFilter.add(glbTypeIc);

        JsonLogFilter fbEvent = new JsonLogFilter();
        fbEvent.setJsonPath("$.glb_x");
        fbEvent.setValueFilter("SIGNIN_FB");
        fbLoginPvFilter.add(fbEvent);
        fbLoginPvFilter.add(pageSubTypeF);
        //fbLoginPvFilter.add(platform);
        fbLoginPv.setJsonLogFilters(fbLoginPvFilter);
        reportQuotaFieldConfigs.add(fbLoginPv);

        //fb登录点击UV
        ReportQuotaFieldConfig fbLoginUv = new ReportQuotaFieldConfig();
        fbLoginUv.setQuotaFieldName("fb_login_click_uv");
        fbLoginUv.setDefaultValue("_skip");
        fbLoginUv.setExtractValueJsonPath("$.glb_od");
        fbLoginUv.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件 glb_t=ic，glb_x=SIGNIN_FB,glb_s=f01计算glb_od的去重后的数量

        fbLoginUv.setJsonLogFilters(fbLoginPvFilter);
        reportQuotaFieldConfigs.add(fbLoginUv);

        //购物车google登录点击PV
        ReportQuotaFieldConfig carGoogleLoginPv = new ReportQuotaFieldConfig();
        carGoogleLoginPv.setQuotaFieldName("car_google_login_pv");
        carGoogleLoginPv.setDefaultValue(0);
        carGoogleLoginPv.setExtractValueJsonPath("$.glb_od");
        carGoogleLoginPv.setValueEnum("countOneWithFilter");
        //过滤条件 glb_t=ic，glb_x=SIGNIN_CART_GOOGLE,glb_s=d01计算glb_od的数量
        List<JsonLogFilter> carGoogleLoginPvFilter = new ArrayList<>();
        carGoogleLoginPvFilter.add(glbTypeIc);

        JsonLogFilter cartGoogleEvent = new JsonLogFilter();
        cartGoogleEvent.setJsonPath("$.glb_x");
        cartGoogleEvent.setValueFilter("SIGNIN_CART_GOOGLE");
        carGoogleLoginPvFilter.add(cartGoogleEvent);
        carGoogleLoginPvFilter.add(pageSubType);
        //carGoogleLoginPvFilter.add(platform);
        carGoogleLoginPv.setJsonLogFilters(carGoogleLoginPvFilter);
        reportQuotaFieldConfigs.add(carGoogleLoginPv);

        //购物车google登录点击UV
        ReportQuotaFieldConfig carGoogleLoginUv = new ReportQuotaFieldConfig();
        carGoogleLoginUv.setQuotaFieldName("car_google_login_uv");
        carGoogleLoginUv.setDefaultValue("_skip");
        carGoogleLoginUv.setExtractValueJsonPath("$.glb_od");
        carGoogleLoginUv.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件 glb_t=ic，glb_x=SIGNIN_CART_GOOGLE,glb_s=d01计算glb_od的去重后的数量
        carGoogleLoginUv.setJsonLogFilters(carGoogleLoginPvFilter);
        reportQuotaFieldConfigs.add(carGoogleLoginUv);

        //google登录点击PV
        ReportQuotaFieldConfig googleLoginPv = new ReportQuotaFieldConfig();
        googleLoginPv.setQuotaFieldName("google_login_click_pv");
        googleLoginPv.setDefaultValue(0);
        googleLoginPv.setExtractValueJsonPath("$.glb_od");
        googleLoginPv.setValueEnum("countOneWithFilter");
        //过滤条件 glb_t=ic，glb_x=SIGNIN_GOOGLE,glb_s=f01计算glb_od的数量
        List<JsonLogFilter> googleLoginPvFilter = new ArrayList<>();
        googleLoginPvFilter.add(glbTypeIc);

        JsonLogFilter googleEvent = new JsonLogFilter();
        googleEvent.setJsonPath("$.glb_x");
        googleEvent.setValueFilter("SIGNIN_GOOGLE");
        googleLoginPvFilter.add(googleEvent);
        googleLoginPvFilter.add(pageSubTypeF);
        //googleLoginPvFilter.add(platform);
        googleLoginPv.setJsonLogFilters(googleLoginPvFilter);
        reportQuotaFieldConfigs.add(googleLoginPv);

        //google登录点击UV
        ReportQuotaFieldConfig googleLoginUv = new ReportQuotaFieldConfig();
        googleLoginUv.setQuotaFieldName("google_login_click_uv");
        googleLoginUv.setDefaultValue("_skip");
        googleLoginUv.setExtractValueJsonPath("$.glb_od");
        googleLoginUv.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件 glb_t=ic，glb_x=SIGNIN_GOOGLE,glb_s=f01计算glb_od的去重后的数量
        googleLoginUv.setJsonLogFilters(googleLoginPvFilter);
        reportQuotaFieldConfigs.add(googleLoginUv);

        //购物车忘记密码点击PV
        ReportQuotaFieldConfig carForgotPwdPv = new ReportQuotaFieldConfig();
        carForgotPwdPv.setQuotaFieldName("car_forgot_pwd_pv");
        carForgotPwdPv.setDefaultValue(0);
        carForgotPwdPv.setExtractValueJsonPath("$.glb_od");
        carForgotPwdPv.setValueEnum("countOneWithFilter");
        //过滤条件glb_t=ic，glb_x=FORGET_CART_PASSWORD,glb_s=d01计算glb_od的数量
        List<JsonLogFilter> carForgotPwdPvFilter = new ArrayList<>();
        carForgotPwdPvFilter.add(glbTypeIc);

        JsonLogFilter forgotCarPwdEvent = new JsonLogFilter();
        forgotCarPwdEvent.setJsonPath("$.glb_x");
        forgotCarPwdEvent.setValueFilter("FORGET_CART_PASSWORD");
        carForgotPwdPvFilter.add(forgotCarPwdEvent);
        carForgotPwdPvFilter.add(pageSubType);
        //carForgotPwdPvFilter.add(platform);
        carForgotPwdPv.setJsonLogFilters(carForgotPwdPvFilter);
        reportQuotaFieldConfigs.add(carForgotPwdPv);

        //购物车忘记密码点击UV
        ReportQuotaFieldConfig carForgotPwdUv = new ReportQuotaFieldConfig();
        carForgotPwdUv.setQuotaFieldName("car_forgot_pwd_uv");
        carForgotPwdUv.setDefaultValue("_skip");
        carForgotPwdUv.setExtractValueJsonPath("$.glb_od");
        carForgotPwdUv.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件 glb_t=ic，glb_x=FORGET_CART_PASSWORD,glb_s=d01计算glb_od的去重后的数量
        carForgotPwdUv.setJsonLogFilters(carForgotPwdPvFilter);
        reportQuotaFieldConfigs.add(carForgotPwdUv);

        //忘记密码点击PV
        ReportQuotaFieldConfig forgotPwdPv = new ReportQuotaFieldConfig();
        forgotPwdPv.setQuotaFieldName("forgot_pwd_pv");
        forgotPwdPv.setDefaultValue(0);
        forgotPwdPv.setExtractValueJsonPath("$.glb_od");
        forgotPwdPv.setValueEnum("countOneWithFilter");
        //过滤条件 glb_t=ic，glb_x=FORGET_PASSWORD,glb_s=f01计算glb_od的数量
        List<JsonLogFilter> forgotPwdPvFilter = new ArrayList<>();
        forgotPwdPvFilter.add(glbTypeIc);

        JsonLogFilter forgotPwdEvent = new JsonLogFilter();
        forgotPwdEvent.setJsonPath("$.glb_x");
        forgotPwdEvent.setValueFilter("FORGET_PASSWORD");
        forgotPwdPvFilter.add(forgotPwdEvent);
        forgotPwdPvFilter.add(pageSubTypeF);
        //forgotPwdPvFilter.add(platform);
        forgotPwdPv.setJsonLogFilters(forgotPwdPvFilter);
        reportQuotaFieldConfigs.add(forgotPwdPv);

        //忘记密码点击UV
        ReportQuotaFieldConfig forgotPwdUv = new ReportQuotaFieldConfig();
        forgotPwdUv.setQuotaFieldName("forgot_pwd_uv");
        forgotPwdUv.setDefaultValue("_skip");
        forgotPwdUv.setExtractValueJsonPath("$.glb_od");
        forgotPwdUv.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件 glb_t=ic，glb_x=FORGET_PASSWORD,glb_s=f01计算glb_od的去重后的数量
        forgotPwdUv.setJsonLogFilters(forgotPwdPvFilter);
        reportQuotaFieldConfigs.add(forgotPwdUv);

        //订单确认页PV
        ReportQuotaFieldConfig orderConfirmPv = new ReportQuotaFieldConfig();
        orderConfirmPv.setQuotaFieldName("order_confirm_pv");
        orderConfirmPv.setDefaultValue(0);
        orderConfirmPv.setExtractValueJsonPath("$.glb_od");
        orderConfirmPv.setValueEnum("countOneWithFilter");
        //过滤条件 glb_t=ie，glb_s=d05,glb_plf=pc,计算glb_od的数量
        List<JsonLogFilter> orderConfirmPvFilter = new ArrayList<>();
        orderConfirmPvFilter.add(glbType);
        JsonLogFilter pageMainType3 = new JsonLogFilter();
        pageMainType3.setJsonPath("$.glb_s");
        pageMainType3.setValueFilter("d05");
        orderConfirmPvFilter.add(pageMainType3);
       // orderConfirmPvFilter.add(platform);
        orderConfirmPv.setJsonLogFilters(orderConfirmPvFilter);
        reportQuotaFieldConfigs.add(orderConfirmPv);

        // 订单确认页UV
        ReportQuotaFieldConfig orderConfirmUv = new ReportQuotaFieldConfig();
        orderConfirmUv.setQuotaFieldName("order_confirm_uv");
        orderConfirmUv.setDefaultValue("_skip");
        orderConfirmUv.setExtractValueJsonPath("$.glb_od");
        orderConfirmUv.setValueEnum("quotaStringValueExtractFromLog");
        //过滤条件 glb_t=ie，glb_s=d05,glb_plf=pc,计算glb_od的去重后的数量
        orderConfirmUv.setJsonLogFilters(orderConfirmPvFilter);
        reportQuotaFieldConfigs.add(orderConfirmUv);


        //place your order按钮点击PV
        ReportQuotaFieldConfig placeOrderPv = new ReportQuotaFieldConfig();
        placeOrderPv.setQuotaFieldName("placeorder_pv");
        placeOrderPv.setDefaultValue(0);
        placeOrderPv.setExtractValueJsonPath("$.glb_od");
        placeOrderPv.setValueEnum("countOneWithFilter");
        //过滤条件 glb_t=ic，glb_s=d05,glb_x=placeorder,glb_plf=pc,计算glb_od的数量
        List<JsonLogFilter> placeOrderPvFilter = new ArrayList<>();
        placeOrderPvFilter.add(glbTypeIc);
        placeOrderPvFilter.add(pageMainType3);

        JsonLogFilter placeorderEvent = new JsonLogFilter();
        placeorderEvent.setJsonPath("$.glb_x");
        placeorderEvent.setValueFilter("placeorder");
        placeOrderPvFilter.add(placeorderEvent);
//        placeOrderPvFilter.add(platform);
        placeOrderPv.setJsonLogFilters(placeOrderPvFilter);
        reportQuotaFieldConfigs.add(placeOrderPv);


        //placeorder按钮点击UV 原始版本
        ReportQuotaFieldConfig placeOrderUv = new ReportQuotaFieldConfig();
        placeOrderUv.setQuotaFieldName("placeorder_uv");
        placeOrderUv.setDefaultValue("_skip");
        placeOrderUv.setExtractValueJsonPath("$.glb_od");
        placeOrderUv.setValueEnum("quotaStringValueExtractFromLog");

        //过滤条件 glb_t=ic，glb_s=d05,glb_x=placeorder,glb_plf=pc,计算glb_od的去重后的数量
        placeOrderUv.setJsonLogFilters(placeOrderPvFilter);
        reportQuotaFieldConfigs.add(placeOrderUv);


        ReportQuotaFieldConfig btsQuota = new ReportQuotaFieldConfig();
        btsQuota.setQuotaFieldName("bts");
        btsQuota.setDefaultValue(bts);
        btsQuota.setExtractValueJsonPath("$.glb_bts");
        //PC端extractBtsMapValueFromLog   APP端:extractMapValueFromLog
        btsQuota.setValueEnum("extractBtsMapValueFromLog");
        reportQuotaFieldConfigs.add(btsQuota);

        ReportQuotaFieldConfig platform=new ReportQuotaFieldConfig();
        platform.setQuotaFieldName("platform");
        platform.setDefaultValue("_skip");
        platform.setExtractValueJsonPath("$.glb_plf");
        platform.setValueEnum("quotaStringValueExtractFromLog");
        reportQuotaFieldConfigs.add(platform);

        // 时间戳字段
        ReportQuotaFieldConfig timestamp = new ReportQuotaFieldConfig();
        timestamp.setQuotaFieldName("timestamp");
        timestamp.setDefaultValue(0);
        timestamp.setExtractValueJsonPath("$.timestamp");
        timestamp.setValueEnum("quotaLongValueExtractFromLog");
        reportQuotaFieldConfigs.add(timestamp);

    }

}
