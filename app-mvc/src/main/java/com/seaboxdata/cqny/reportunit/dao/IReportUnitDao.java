package com.seaboxdata.cqny.reportunit.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.reportunit.entity.UnitEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
public interface IReportUnitDao {

    @Insert("INSERT INTO report_unit_info (\n" +
            "\tunit_name,\n" +
            "\tcreate_time,\n" +
            "\tcreate_user,\n" +
            "\torigin_id,\n" +
            "\tstatus\n" +
            ")\n" +
            "VALUES\n" +
            "\t(\n" +
            "\t#{ unit_name },sysdate(),#{ create_user },#{ origin_id },#{ status }\n" +
            "\t)")
    void addReportUnit(UnitEntity reportUnit);

    @Delete("delete from report_unit_info where unit_id = #{unitId}")
    void deleteById(@Param("unitId") String unit_id);

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
    void updateReportUnit(UnitEntity reportUnit);

    @Select("SELECT\n" +
            "\ta.create_time,\n" +
            "\ta.`status`,\n" +
            "\ta.unit_id,\n" +
            "\ta.unit_name,\n" +
            "\tb.origin_name,\n" +
            "\tc.user_name,\n" +
            "\tb.create_user,\n" +
            "\ta.origin_id\n" +
            "FROM\n" +
            "\treport_unit_info a\n" +
            "LEFT JOIN sys_origin b ON a.origin_id = b.origin_id\n" +
            "LEFT JOIN `user` c ON a.create_user = c.user_id\n" +
            "WHERE\n" +
            "\t1 = 1")
    Page<UnitEntity> listReportUnit(@Param("currPage") int currPage, @Param("pageSize") int pageSize);

}