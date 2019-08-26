package com.seaboxdata.cqny.datamove.dao;

import com.seaboxdata.cqny.datamove.entity.DataMoveConfig;
import com.seaboxdata.cqny.record.entity.ReportCustomer;
import com.seaboxdata.cqny.record.entity.ReportCustomerData;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface IReportDataMoveDao {

    @Select("select  " +
            "tc.defined_id," +
            "tc.old_unit_table_name," +
            "tc.new_unit_id," +
            "rc.old_unit_row_name," +
            "rc.new_unit_colum_id," +
            "cc.old_unit_colum_name," +
            "cc.new_unit_dim_id from data_move_table_config tc " +
            "left join data_move_col_config cc on " +
            "tc.old_unit_table_name = cc.old_unit_table_name " +
            "left join data_move_row_config rc on " +
            "tc.old_unit_table_name = rc.old_unit_table_name ")
    List<DataMoveConfig> getAllDataMoveConfig();

    @Select("select * from data_move_config ")
    List<DataMoveConfig> getAllDataMoveConfigOneTable();

    @Select("select * from ${tableName} where bankid = #{originId} order by BANKID,DDATE,RID")
    List<Map<String,Object>> getOldDataForTable(@Param("tableName") String tableName,@Param("originId") Object originId);

    @Select("select distinct BANKID from ${tableName}")
    List<Map<String, Object>> getOldDataForTableOrgs(@Param("tableName") String tableName);

    @Insert("insert into report_customer_data (report_id,unit_id,report_group_id,colum_id,dimensions_id,report_data) values" +
            "(#{report_id},#{unit_id},#{report_group_id},#{colum_id},#{dimensions_id},#{report_data})")
    void saveReportCustomerData(ReportCustomerData reportCustomerData);

    @Insert("<script>" +
            "update report_customer_data set report_data = #{report_data} " +
            "where " +
            "report_id=#{report_id} and " +
            "colum_id=#{colum_id}  " +
            " <if test=\"dimensions_id != null and dimensions_id!='' \"> and dimensions_id=#{dimensions_id} </if> " +
            "</script>")
    void updateReportCustomerData(ReportCustomerData reportCustomerData);

    @Delete("delete from report_customer_data where report_id in (select report_id from report_customer where report_defined_id = #{report_defined_id})")
    void cleanReportCustData(@Param("report_defined_id") Integer report_defined_id);

    @Delete("delete from report_customer where report_defined_id= #{report_defined_id}")
    void cleanReportCust(@Param("report_defined_id") Integer report_defined_id);

    @Select("select * from report_customer where report_defined_id= #{report_defined_id} and report_origin = #{originId}")
    ReportCustomer getOriginReportCustomerByReportDefined(@Param("report_defined_id") Integer report_defined_id,@Param("originId") Object originId);

    @Delete("delete from report_customer_data where report_id = #{reportId} and unit_id = #{unitId} ")
    void removeTreeData(@Param("reportId") Integer reportId,@Param("unitId") String unitId);
}
