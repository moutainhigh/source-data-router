package com.globalegrow.dy.model;

import java.io.Serializable;
import java.util.Date;

public class BtsReportKylinConfig implements Serializable {
    private static final long serialVersionUID = 4066895066145074228L;
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column DY_BTS_REPORT_KYLIN_CONFIG.id
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column DY_BTS_REPORT_KYLIN_CONFIG.bts_plan_id
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    private Long btsPlanId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column DY_BTS_REPORT_KYLIN_CONFIG.description
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    private String description;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column DY_BTS_REPORT_KYLIN_CONFIG.kylin_user_name_password
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    private String kylinUserNamePassword;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column DY_BTS_REPORT_KYLIN_CONFIG.kylin_query_adress
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    private String kylinQueryAdress;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column DY_BTS_REPORT_KYLIN_CONFIG.create_time
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column DY_BTS_REPORT_KYLIN_CONFIG.update_time
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    private Date updateTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column DY_BTS_REPORT_KYLIN_CONFIG.create_by
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    private String createBy;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column DY_BTS_REPORT_KYLIN_CONFIG.kylin_project_name
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    private String kylinProjectName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column DY_BTS_REPORT_KYLIN_CONFIG.query_type
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    private String queryType;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column DY_BTS_REPORT_KYLIN_CONFIG.bts_product_line_code
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    private String btsProductLineCode;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column DY_BTS_REPORT_KYLIN_CONFIG.kylin_query_sql
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    private String kylinQuerySql;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column DY_BTS_REPORT_KYLIN_CONFIG.id
     *
     * @return the value of DY_BTS_REPORT_KYLIN_CONFIG.id
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column DY_BTS_REPORT_KYLIN_CONFIG.id
     *
     * @param id the value for DY_BTS_REPORT_KYLIN_CONFIG.id
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column DY_BTS_REPORT_KYLIN_CONFIG.bts_plan_id
     *
     * @return the value of DY_BTS_REPORT_KYLIN_CONFIG.bts_plan_id
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    public Long getBtsPlanId() {
        return btsPlanId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column DY_BTS_REPORT_KYLIN_CONFIG.bts_plan_id
     *
     * @param btsPlanId the value for DY_BTS_REPORT_KYLIN_CONFIG.bts_plan_id
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    public void setBtsPlanId(Long btsPlanId) {
        this.btsPlanId = btsPlanId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column DY_BTS_REPORT_KYLIN_CONFIG.description
     *
     * @return the value of DY_BTS_REPORT_KYLIN_CONFIG.description
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    public String getDescription() {
        return description;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column DY_BTS_REPORT_KYLIN_CONFIG.description
     *
     * @param description the value for DY_BTS_REPORT_KYLIN_CONFIG.description
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column DY_BTS_REPORT_KYLIN_CONFIG.kylin_user_name_password
     *
     * @return the value of DY_BTS_REPORT_KYLIN_CONFIG.kylin_user_name_password
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    public String getKylinUserNamePassword() {
        return kylinUserNamePassword;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column DY_BTS_REPORT_KYLIN_CONFIG.kylin_user_name_password
     *
     * @param kylinUserNamePassword the value for DY_BTS_REPORT_KYLIN_CONFIG.kylin_user_name_password
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    public void setKylinUserNamePassword(String kylinUserNamePassword) {
        this.kylinUserNamePassword = kylinUserNamePassword == null ? null : kylinUserNamePassword.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column DY_BTS_REPORT_KYLIN_CONFIG.kylin_query_adress
     *
     * @return the value of DY_BTS_REPORT_KYLIN_CONFIG.kylin_query_adress
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    public String getKylinQueryAdress() {
        return kylinQueryAdress;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column DY_BTS_REPORT_KYLIN_CONFIG.kylin_query_adress
     *
     * @param kylinQueryAdress the value for DY_BTS_REPORT_KYLIN_CONFIG.kylin_query_adress
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    public void setKylinQueryAdress(String kylinQueryAdress) {
        this.kylinQueryAdress = kylinQueryAdress == null ? null : kylinQueryAdress.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column DY_BTS_REPORT_KYLIN_CONFIG.create_time
     *
     * @return the value of DY_BTS_REPORT_KYLIN_CONFIG.create_time
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column DY_BTS_REPORT_KYLIN_CONFIG.create_time
     *
     * @param createTime the value for DY_BTS_REPORT_KYLIN_CONFIG.create_time
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column DY_BTS_REPORT_KYLIN_CONFIG.update_time
     *
     * @return the value of DY_BTS_REPORT_KYLIN_CONFIG.update_time
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column DY_BTS_REPORT_KYLIN_CONFIG.update_time
     *
     * @param updateTime the value for DY_BTS_REPORT_KYLIN_CONFIG.update_time
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column DY_BTS_REPORT_KYLIN_CONFIG.create_by
     *
     * @return the value of DY_BTS_REPORT_KYLIN_CONFIG.create_by
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    public String getCreateBy() {
        return createBy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column DY_BTS_REPORT_KYLIN_CONFIG.create_by
     *
     * @param createBy the value for DY_BTS_REPORT_KYLIN_CONFIG.create_by
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column DY_BTS_REPORT_KYLIN_CONFIG.kylin_project_name
     *
     * @return the value of DY_BTS_REPORT_KYLIN_CONFIG.kylin_project_name
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    public String getKylinProjectName() {
        return kylinProjectName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column DY_BTS_REPORT_KYLIN_CONFIG.kylin_project_name
     *
     * @param kylinProjectName the value for DY_BTS_REPORT_KYLIN_CONFIG.kylin_project_name
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    public void setKylinProjectName(String kylinProjectName) {
        this.kylinProjectName = kylinProjectName == null ? null : kylinProjectName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column DY_BTS_REPORT_KYLIN_CONFIG.query_type
     *
     * @return the value of DY_BTS_REPORT_KYLIN_CONFIG.query_type
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    public String getQueryType() {
        return queryType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column DY_BTS_REPORT_KYLIN_CONFIG.query_type
     *
     * @param queryType the value for DY_BTS_REPORT_KYLIN_CONFIG.query_type
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    public void setQueryType(String queryType) {
        this.queryType = queryType == null ? null : queryType.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column DY_BTS_REPORT_KYLIN_CONFIG.bts_product_line_code
     *
     * @return the value of DY_BTS_REPORT_KYLIN_CONFIG.bts_product_line_code
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    public String getBtsProductLineCode() {
        return btsProductLineCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column DY_BTS_REPORT_KYLIN_CONFIG.bts_product_line_code
     *
     * @param btsProductLineCode the value for DY_BTS_REPORT_KYLIN_CONFIG.bts_product_line_code
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    public void setBtsProductLineCode(String btsProductLineCode) {
        this.btsProductLineCode = btsProductLineCode == null ? null : btsProductLineCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column DY_BTS_REPORT_KYLIN_CONFIG.kylin_query_sql
     *
     * @return the value of DY_BTS_REPORT_KYLIN_CONFIG.kylin_query_sql
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    public String getKylinQuerySql() {
        return kylinQuerySql;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column DY_BTS_REPORT_KYLIN_CONFIG.kylin_query_sql
     *
     * @param kylinQuerySql the value for DY_BTS_REPORT_KYLIN_CONFIG.kylin_query_sql
     *
     * @mbg.generated Tue Aug 21 18:10:31 CST 2018
     */
    public void setKylinQuerySql(String kylinQuerySql) {
        this.kylinQuerySql = kylinQuerySql == null ? null : kylinQuerySql.trim();
    }

    @Override
    public String toString() {
        return "BtsReportKylinConfig{" +
                "id=" + id +
                ", btsPlanId=" + btsPlanId +
                ", description='" + description + '\'' +
                ", kylinUserNamePassword='" + kylinUserNamePassword + '\'' +
                ", kylinQueryAdress='" + kylinQueryAdress + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", createBy='" + createBy + '\'' +
                ", kylinProjectName='" + kylinProjectName + '\'' +
                ", queryType='" + queryType + '\'' +
                ", btsProductLineCode='" + btsProductLineCode + '\'' +
                ", kylinQuerySql='" + kylinQuerySql + '\'' +
                '}';
    }

   /* public String getCacheKey() {
        return this.hashCode() + "";
    }*/
}