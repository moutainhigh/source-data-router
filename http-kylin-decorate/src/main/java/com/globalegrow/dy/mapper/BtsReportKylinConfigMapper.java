package com.globalegrow.dy.mapper;

import com.globalegrow.dy.model.BtsReportKylinConfig;
import com.globalegrow.dy.model.BtsReportKylinConfigExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BtsReportKylinConfigMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DY_BTS_REPORT_KYLIN_CONFIG
     *
     * @mbg.generated Thu Aug 09 20:32:57 CST 2018
     */
    long countByExample(BtsReportKylinConfigExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DY_BTS_REPORT_KYLIN_CONFIG
     *
     * @mbg.generated Thu Aug 09 20:32:57 CST 2018
     */
    int deleteByExample(BtsReportKylinConfigExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DY_BTS_REPORT_KYLIN_CONFIG
     *
     * @mbg.generated Thu Aug 09 20:32:57 CST 2018
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DY_BTS_REPORT_KYLIN_CONFIG
     *
     * @mbg.generated Thu Aug 09 20:32:57 CST 2018
     */
    int insert(BtsReportKylinConfig record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DY_BTS_REPORT_KYLIN_CONFIG
     *
     * @mbg.generated Thu Aug 09 20:32:57 CST 2018
     */
    int insertSelective(BtsReportKylinConfig record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DY_BTS_REPORT_KYLIN_CONFIG
     *
     * @mbg.generated Thu Aug 09 20:32:57 CST 2018
     */
    List<BtsReportKylinConfig> selectByExampleWithBLOBs(BtsReportKylinConfigExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DY_BTS_REPORT_KYLIN_CONFIG
     *
     * @mbg.generated Thu Aug 09 20:32:57 CST 2018
     */
    List<BtsReportKylinConfig> selectByExample(BtsReportKylinConfigExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DY_BTS_REPORT_KYLIN_CONFIG
     *
     * @mbg.generated Thu Aug 09 20:32:57 CST 2018
     */
    BtsReportKylinConfig selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DY_BTS_REPORT_KYLIN_CONFIG
     *
     * @mbg.generated Thu Aug 09 20:32:57 CST 2018
     */
    int updateByExampleSelective(@Param("record") BtsReportKylinConfig record, @Param("example") BtsReportKylinConfigExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DY_BTS_REPORT_KYLIN_CONFIG
     *
     * @mbg.generated Thu Aug 09 20:32:57 CST 2018
     */
    int updateByExampleWithBLOBs(@Param("record") BtsReportKylinConfig record, @Param("example") BtsReportKylinConfigExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DY_BTS_REPORT_KYLIN_CONFIG
     *
     * @mbg.generated Thu Aug 09 20:32:57 CST 2018
     */
    int updateByExample(@Param("record") BtsReportKylinConfig record, @Param("example") BtsReportKylinConfigExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DY_BTS_REPORT_KYLIN_CONFIG
     *
     * @mbg.generated Thu Aug 09 20:32:57 CST 2018
     */
    int updateByPrimaryKeySelective(BtsReportKylinConfig record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DY_BTS_REPORT_KYLIN_CONFIG
     *
     * @mbg.generated Thu Aug 09 20:32:57 CST 2018
     */
    int updateByPrimaryKeyWithBLOBs(BtsReportKylinConfig record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DY_BTS_REPORT_KYLIN_CONFIG
     *
     * @mbg.generated Thu Aug 09 20:32:57 CST 2018
     */
    int updateByPrimaryKey(BtsReportKylinConfig record);
}