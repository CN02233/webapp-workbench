package com.seaboxdata.cqny.record.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.ReportCustomer;
import com.seaboxdata.cqny.record.entity.ReportCustomerData;
import com.seaboxdata.cqny.reportunit.entity.UnitEntity;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;
import java.util.List;

public interface IReportCustomerDao {

    @Select("select rc.* from report_customer rc inner join " +
            "(select origin_id from user_origin_assign where user_id = #{userId}) org on " +
            "rc.report_origin=org.origin_id")
    Page<ReportCustomer> pageReport(@Param("currPage") Integer currPage,
                                    @Param("pageSize") Integer pageSize,
                                    @Param("userId") Integer userId);


    @Select("select * from report_customer where report_id = #{reportId}")
    @Results(value={
            @Result(property = "report_id",column = "report_id"),
            @Result(property = "unitEntities",column = "report_id" ,javaType= List.class, many=@Many(select="getAllUnitEntityByReportId"))
    })
    ReportCustomer checkReportCustomer(String reportId);

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
    List<UnitEntity> getAllUnitEntityByReportId(String report_id);

    @Select("select * from report_customer_data where report_id=#{reportId} and unit_id=#{unitId}")
    List<ReportCustomerData> getColumDatas(@Param("reportId") String reportId, @Param("unitId") String unitId);

    @Update("update report_customer_data set report_data = #{report_data} where report_id=#{report_id} and unit_id=#{unit_id} and colum_id=#{colum_id}")
    void updateUnitContext(ReportCustomerData columDatas);

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
    UnitEntity getUnitByReportStep(String reportId);

    @Update("update report_customer set active_unit= #{nextStepUnit} where report_id = #{reportId}")
    void updateStep(@Param("reportId") String reportId,@Param("nextStepUnit") Integer nextStepUnit);

    @Delete("delete from report_customer_data where unit_id=#{unit_id}")
    void removeUnitContextData(String unit_id);

    @Insert("insert into report_customer_data (report_id,unit_id,report_group_id,colum_id,dimensions_id,report_data) " +
            "values (#{report_id},#{unit_id},#{report_group_id},#{colum_id},#{dimensions_id},#{report_data})")
    void insertUnitContext(ReportCustomerData columData);

    @Select("select * from report_customer_data where " +
            "report_id=#{reportId} and unit_id=#{unitId} and colum_id=#{columId} and dimensions_id=#{dimensionsId}")
    ReportCustomerData getSimpleReportCustomerDataBydimensions(String reportId, String unitId,String columId, String dimensionsId);
}
