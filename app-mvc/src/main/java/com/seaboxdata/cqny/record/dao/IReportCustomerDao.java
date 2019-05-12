package com.seaboxdata.cqny.record.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.ReportCustomer;
import com.seaboxdata.cqny.record.entity.ReportCustomerData;
import com.seaboxdata.cqny.record.entity.UnitDefined;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IReportCustomerDao {

    @Select("select rc.* from report_customer rc inner join " +
            "(select origin_id from user_origin_assign where user_id = #{userId}) org on " +
            "rc.report_origin=org.origin_id")
    Page<ReportCustomer> pageReport(@Param("currPage") Integer currPage,
                                    @Param("pageSize") Integer pageSize,
                                    @Param("userId") Integer userId);

    @Select("<script>" +
            "select rc.* from report_customer rc where rc.report_origin in " +
            "<foreach item='item' index='index' collection='originParams' open='(' separator=',' close=')'> " +
            "#{item}" +
            "</foreach>" +
            "</script>")
    Page<ReportCustomer> pageReportByOrigins(@Param("currPage") Integer currPage,
                                    @Param("pageSize") Integer pageSize,
                                    @Param("originParams") List<Integer> originParams);


    @Select("select * from report_customer where report_id = #{reportId}")
    @Results(value={
            @Result(property = "report_defined_id",column = "report_defined_id"),
            @Result(property = "unitEntities",column = "report_defined_id" ,javaType= List.class, many=@Many(select="getAllUnitEntityByReportId"))
    })
    ReportCustomer checkReportCustomer(String reportId);

    @Insert("insert into report_customer " +
            "(report_defined_id,report_name,report_origin,create_date,report_start_date,report_end_date,active_unit,pass_approve,pass_review," +
            "report_data_start," +
            "report_data_end) " +
            "values " +
            "(#{report_defined_id},#{report_name},#{report_origin},#{create_date},#{report_start_date},#{report_end_date},#{active_unit}" +
            ",#{pass_approve},#{pass_review}" +
            ",#{report_data_start},#{report_data_end})")
    @Options(useGeneratedKeys = true, keyProperty = "report_id", keyColumn = "report_id")
    void createReportCustomer(ReportCustomer reportCustomer);

    @Update("update report_customer set " +
            "report_name = #{report_name}," +
            "create_date = #{create_date}," +
            "report_start_date = #{report_start_date}," +
            "report_end_date = #{report_end_date}," +
            "report_status = #{report_status}," +
            "active_unit = #{active_unit}," +
            "pass_auth = #{pass_auth} where report_id = #{report_id}")
    void updateReportCustomer(ReportCustomer reportCustomer);

    @Update("<script>" +
            "update report_customer_data set report_data = #{dataValue} where report_id=#{reportId} and unit_id=#{unitId} " +
            " <if test=\"columId != null\"> and colum_id = #{columId} </if> " +
            " <if test=\"dimId != null\"> and dimensions_id = #{dimId} </if> " +
            " <if test=\"reportGroupId != null\"> and report_group_id = #{reportGroupId} </if> "+
            "</script>")
    void updateReportCustomerData(
            @Param("reportId") String reportId,
            @Param("unitId") String unitId,
            @Param("columId") String columId,
            @Param("dimId") String dimId,
            @Param("reportGroupId") String reportGroupId,
            @Param("dataValue") Object dataValue);

    @Update("update report_customer set "+
            "report_status = #{status} where report_id = #{reportId}")
    void updateReportCustomerStatus(@Param("reportId") String reportId,@Param("status") String status);

    @Select("select " +
            "rui.create_time," +
            "rui.`status`," +
            "rui.unit_id," +
            "rui.unit_name," +
            "rui.report_defined_id," +
            "rui.unit_type," +
            "rui.unit_order," +
            "rui.origin_id " +
            " from report_unit_info rui where rui.report_defined_id = #{report_id} order by rui.unit_order")
    List<UnitDefined> getAllUnitEntityByReportId(String report_id);

    @Select("select * from report_customer_data where report_id=#{reportId} and unit_id=#{unitId} order by unit_id,colum_order")
    List<ReportCustomerData> getColumDatas(@Param("reportId") String reportId, @Param("unitId") String unitId);

    @Update("update report_customer_data set report_data = #{report_data} where report_id=#{report_id} and unit_id=#{unit_id} and colum_id=#{colum_id}")
    void updateUnitContext(ReportCustomerData columDatas);

    @Update("update report_customer_data set report_data = #{report_data} where report_id=#{report_id} and unit_id=#{unit_id} and " +
            "( colum_id=#{colum_id} or dimensions_id=#{dimensions_id})")
    void updateUnitContextSimple(ReportCustomerData columDatas);

    @Select("select * from report_customer_data where report_id=#{reportId} and unit_id=#{unitId} and colum_id=#{columId}")
    ReportCustomerData getSimpleReportCustomerData(@Param("reportId") String reportId, @Param("unitId") String unitId,@Param("columId") String columId);

    @Select("select " +
            "rui.create_time," +
            "rui.`status`," +
            "rui.unit_id," +
            "rui.unit_name," +
            "rui.report_defined_id," +
            "rui.unit_type," +
            "rui.origin_id " +
            " from report_customer rd inner join report_unit_info rui on rd.report_id=#{reportId} and  rd.active_unit=rui.unit_id ")
    UnitDefined getUnitByReportStep(String reportId);

    @Update("update report_customer set active_unit= #{nextStepUnit} where report_id = #{reportId}")
    void updateStep(@Param("reportId") String reportId,@Param("nextStepUnit") Integer nextStepUnit);

    @Delete("delete from report_customer_data where unit_id=#{unit_id}")
    void removeUnitContextData(String unit_id);

    @Insert("insert into report_customer_data (report_id,unit_id,report_group_id,colum_id,dimensions_id,report_data,colum_order) " +
            "values (#{report_id},#{unit_id},#{report_group_id},#{colum_id},#{dimensions_id},#{report_data},#{colum_order})")
    void insertUnitContext(ReportCustomerData columData);

    @Select("select * from report_customer_data where " +
            "report_id=#{reportId} and unit_id=#{unitId} and colum_id=#{columId} and dimensions_id=#{dimensionsId}")
    ReportCustomerData getSimpleReportCustomerDataBydimensions(@Param("reportId") String reportId,@Param("unitId") String unitId
            ,@Param("columId") String columId,@Param("dimensionsId") String dimensionsId);

    @Select("select * from report_customer_data where " +
            "report_id=#{reportId} and unit_id=#{unitId} and dimensions_id=#{dimensionsId}")
    ReportCustomerData getSimpleReportCustomerDataByDimId(
            @Param("reportId") String reportId,
            @Param("unitId") String unitId
            ,@Param("dimensionsId") String dimensionsId);

    @Select("select * from report_customer_data where " +
            "report_id=#{reportId} and unit_id=#{unitId} and dimensions_id=#{dimensionsId}")
    List<ReportCustomerData> getSimpleReportCustomerDatasByDimId(
            @Param("reportId") String reportId,
            @Param("unitId") String unitId
            ,@Param("dimensionsId") String dimensionsId);

    @Select("select * from report_customer_data where " +
            "report_id=#{reportId} and unit_id=#{unitId} and colum_id=#{columId}")
    List<ReportCustomerData> getSimpleReportCustomerDatasByColId(
            @Param("reportId") String reportId,
            @Param("unitId") String unitId
            ,@Param("columId") String columId);

    @Update("update report_customer_data set report_data = #{report_data} where report_id=#{report_id} and unit_id=#{unit_id} and colum_id=#{colum_id} and dimensions_id=#{dimensions_id}")
    void updateGridUnitContext(ReportCustomerData columDatas);

    @Select("select sum(report_data) as sum_value from report_customer_data where " +
            "report_id=#{reportId} and unit_id=#{unitId} and dimensions_id = #{dimVal} ")
    BigDecimal sumColumForDimensions(@Param("reportId") String reportId, @Param("unitId") String unitId,@Param("dimVal") String dimVal);

    @Select("select sum(report_data) as sum_value from report_customer_data where report_id=#{reportId} and" +
            "s unit_id=#{unitId} and colum_id = #{dimVal}")
    Object sumColumForColums(@Param("reportId") String reportId, @Param("unitId") String unitId,@Param("dimVal") String dimVal);

    @Select("select rc.* from report_customer rc where rc.report_origin = #{userOriginId} ")
    List<ReportCustomer> getAllReportInfoByOrigin(int userOriginId);

    @Select("<script>" +
            "select rc.*,so.origin_name as report_origin_name from report_customer rc inner join sys_origin so on rc.report_origin in " +
            "<foreach item='item' index='index' collection='originParams' open='(' separator=',' close=')'> " +
            "#{item}" +
            "</foreach> and rc.report_origin=so.origin_id" +
            "</script>")
    List<ReportCustomer> getAllReportInfoByOrigins(@Param("originParams") List<Integer> originParams);

    @Select("select distinct rc.*,so.origin_name as report_origin_name from " +
            "report_customer rc left join " +
            " (select report_defined_id from " +
            " report_customer where report_id = #{reportId}) rct  on rc.report_defined_id =rct.report_defined_id " +
            " inner join sys_origin so on rc.report_origin=so.origin_id")
    List<ReportCustomer> getReportBaseInfo(String reportId);

    @Select("select distinct rc.*,so.origin_name as report_origin_name from " +
            "report_customer rc " +
            " inner join sys_origin so on  rc.report_defined_id =#{reportDefinedId} and rc.report_origin=so.origin_id")
    List<ReportCustomer> getReportBaseInfoByDefinedId(String reportDefinedId);

    @Select("select * from report_customer where report_id = #{reportID}")
    ReportCustomer getReportCustomerByReportID(String reportID);
}
