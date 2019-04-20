package com.seaboxdata.cqny.record.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.UnitDefined;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IReportUnitDao {

    @Insert("INSERT INTO report_unit_info ( " +
            " report_defined_id, " +
            " unit_name, " +
            " create_time, " +
            " create_user, " +
            " unit_type, " +
            " unit_order, " +
            " status " +
            ") " +
            "VALUES " +
            " ( " +
            " #{report_defined_id},#{unit_name},sysdate(),#{create_user},#{unit_type},#{unit_order},#{status} " +
            " )")
    @Options(useGeneratedKeys = true, keyProperty = "unit_id", keyColumn = "unit_id")
    void addReportUnit(UnitDefined reportUnit);

    @Delete("delete from report_unit_info where unit_id = #{unitId}")
    void deleteById(@Param("unitId") String unit_id);

    @Delete("DELETE a,b " +
            "FROM " +
            " report_unit_info a  " +
            "INNER JOIN report_defined_unit_onedim b ON a.unit_id=b.unit_id " +
            " where  " +
            " a.unit_id=#{unitId}")
    void deleteUnionsOneDimById(@Param("unitId") String unit_id);

    @Delete("DELETE a,b,c,d " +
            "FROM " +
            " report_unit_info a  " +
            "INNER JOIN report_defined_unit_multdim b ON a.unit_id=b.unit_id " +
            "INNER JOIN report_defined_unit_multdim_col c ON a.unit_id=c.unit_id " +
            "INNER JOIN report_defined_unit_multdim_dim d ON a.unit_id=d.unit_id " +
            " where  " +
            " a.unit_id= #{unitId}")
    void deleteUnionsMultDimById(@Param("unitId") String unit_id);



    @Update("<script>update report_unit_info <set>"
            +"<if test='origin_id!=null'>"
            +"origin_id=#{origin_id} ,"
            +"</if>"
            +"<if test='unit_name!=null'>"
            +"unit_name=#{unit_name} ,"
            +"</if>"
            +"<if test='status!=null'>"
            +"status=#{status} ,"
            +"</if>"
            +"<if test='create_user!=null'>"
            +"create_user=#{create_user} ,"
            +"</if>"
            +"create_time=sysdate() "
            +"</set>where unit_id = #{unit_id}</script>")
    void updateReportUnit(UnitDefined reportUnit);

    @Select("SELECT " +
            " a.create_time, " +
            " a.`status`, " +
            " a.unit_id, " +
            " a.unit_name, " +
            " b.origin_name, " +
            " c.user_name, " +
            " b.create_user, " +
            " a.unit_type, " +
            " a.unit_order, " +
            " a.report_defined_id, " +
            " a.origin_id " +
            "FROM " +
            " report_unit_info a " +
            "LEFT JOIN sys_origin b ON a.origin_id = b.origin_id " +
            "LEFT JOIN `user` c ON a.create_user = c.user_id " +
            "WHERE " +
            " 1 = 1 and a.report_defined_id = #{reportDefindId} order by a.unit_order")
    Page<UnitDefined> listReportUnit(@Param("currPage") int currPage, @Param("pageSize") int pageSize,@Param("reportDefindId") String reportDefindId);

    @Select("SELECT " +
            "a.create_time," +
            "a.`status`," +
            "a.unit_id," +
            "a.unit_name," +
            "b.origin_name," +
            "c.user_name," +
            "b.create_user," +
            "a.report_defined_id," +
            "a.unit_type," +
            "a.origin_id " +
            "FROM " +
            "report_unit_info a " +
            "LEFT JOIN sys_origin b ON a.origin_id = b.origin_id " +
            "LEFT JOIN `user` c ON a.create_user = c.user_id " +
            "WHERE " +
            "1 = 1 and a.unit_id = #{unit_id} ")
    UnitDefined getReportUnit(String unit_id);

    @Select("SELECT " +
            "a.create_time," +
            "a.`status`," +
            "a.unit_id," +
            "a.unit_name," +
            "a.report_defined_id," +
            "a.unit_type," +
            "a.unit_order," +
            "a.origin_id " +
            "FROM " +
            "report_unit_info a " +
            "WHERE " +
            "1 = 1 and a.report_defined_id = #{reportDefinedId} ")
    List<UnitDefined> getUnitDefinedByReportDefindId(String reportDefinedId);
}
