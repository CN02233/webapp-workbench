package com.seaboxdata.cqny.record.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.Origin;
import com.seaboxdata.cqny.record.entity.ReportCustomer;
import com.seaboxdata.cqny.record.entity.ReportDefinedEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Repository
public interface IReportStatementsDao {

    @Insert("INSERT INTO report_defined (\n" +
            "\tdefined_id,\n" +
            "\tdefined_name,\n" +
            "\tcreate_date,\n" +
            "\tcreate_user,\n" +
            "\torigin_id,\n" +
            "\tstatus\n" +
            ")\n" +
            "VALUES\n" +
            "\t(\n" +
            "\t#{ defined_id },#{ defined_name },sysdate(),#{ create_user },#{ origin_id },#{ status }\n" +
            "\t)")
    @Options(useGeneratedKeys = true, keyProperty = "defined_id", keyColumn = "defined_id")
    void addReportStatements(ReportDefinedEntity reportDefined);

    @Delete("delete from report_defined where defined_id = #{definedId}")
    void deleteById(@Param("definedId") String defined_id);

    @Update("<script>update report_defined <set>"
            +"<if test='origin_id!=null'>"
            +"origin_id=#{origin_id} ,"
            +"</if>"
            +"<if test='defined_name!=null'>"
            +"defined_name=#{defined_name} ,"
            +"</if>"
            +"<if test='status!=null'>"
            +"status=#{status} ,"
            +"</if>"
            +"<if test='create_user!=null'>"
            +"create_user=#{create_user} ,"
            +"</if>"
            +"create_date=sysdate() "
            +"</set>where defined_id = #{defined_id}</script>")
    void updateReportStatements(ReportDefinedEntity reportDefined);

    @Select("SELECT\n" +
            "\ta.create_date,\n" +
            "\ta.`status`,\n" +
            "\ta.defined_id,\n" +
            "\ta.defined_name,\n" +
            "\tb.origin_name,\n" +
            "\tc.user_name,\n" +
            "\tb.create_user,\n" +
            "\ta.origin_id\n" +
            "FROM\n" +
            "\treport_defined a\n" +
            "LEFT JOIN sys_origin b ON a.origin_id = b.origin_id\n" +
            "LEFT JOIN `user` c ON a.create_user = c.user_id\n" +
            "WHERE\n" +
            "\t1 = 1")
    Page<ReportDefinedEntity> listReportStatements(@Param("currPage") int currPage, @Param("pageSize") int pageSize);


    @Select("SELECT" +
            " a.create_date," +
            " a.`status`," +
            " a.defined_id," +
            " a.defined_name," +
            " b.origin_name," +
            " c.user_name," +
            " b.create_user," +
            " a.origin_id" +
            " FROM" +
            " report_defined a" +
            " LEFT JOIN sys_origin b ON a.origin_id = b.origin_id" +
            " LEFT JOIN `user` c ON a.create_user = c.user_id" +
            " WHERE" +
            " defined_id = #{definedId}")
    ReportDefinedEntity getReportDefinedById(Integer definedId);

    @Insert("<script>INSERT INTO report_defined_origin_assign (defined_id,origin_id)\n" +
            "VALUES " +
            "<foreach item=\"item\" index=\"index\" collection=\"originIds\" separator=\",\">" +
            "(#{definedId},#{item})" +
            "</foreach></script>")
    void saveDefinedAndOriginAssign(@Param("originIds")String[] originIds,@Param("definedId") String definedId);

    @Select("SELECT\n" +
            "\ta.origin_id\n" +
            "FROM\n" +
            "\treport_defined_origin_assign a\n" +
            "WHERE\n" +
            "\t1 = 1" +
            " and defined_id= #{definedId}")
    List<String> getDefinedAndOriginAssignById(@Param("definedId") String definedId);

    @Delete("delete from report_defined_origin_assign where defined_id = #{definedId}")
    void delDefinedAndOriginAssign(String definedId);

    /**
     * 监管用户获取报送报表的列表
     * @param currPage
     * @param pageSize
     * @param finalOriginList
     * @return
     */
    @Select("<script>SELECT \n" +
            "\trc.active_unit,\n" +
            "\trc.create_date,\n" +
            "\trc.report_status,\n" +
            "\t(select u.user_name from `user` u where u.user_id=rc.last_modify_user) last_modify_user,\n" +
            "\trc.report_defined_id,\n" +
            "\trc.report_end_date,\n" +
            "\trc.report_id,\n" +
            "\trc.report_name,\n" +
            "\trc.report_origin,\n" +
            "\tso.origin_name as report_origin_name,\n" +
            "\trc.report_start_date\n" +
            "FROM\n" +
            "\treport_customer rc inner join sys_origin so on rc.report_origin=so.origin_id" +
            " LEFT JOIN report_defined rd on rc.report_defined_id=rd.defined_id \n" +
            "and \n" +
            " rc.report_origin IN " +
            "<foreach item='item' index='index' collection='originList' open='(' separator=',' close=')'> " +
            " #{item} " +
            "</foreach>" +
            "</script>")
    Page<ReportCustomer> listReportStatementsByUser(@Param("currPage")int currPage, @Param("pageSize") int pageSize, @Param("originList")Set finalOriginList);

    @Update("update report_defined set status = #{status} where defined_id=#{definedId}")
    void changeDeindStatus(@Param("definedId") String definedId,@Param("status") int status);

    @Select("select so.* from sys_origin so,report_defined_origin_assign rdoa where rdoa.defined_id = #{definedId} and rdoa.origin_id = so.origin_id")
    List<Origin> getDefinedOriginsById(String definedId);

    @Select("<script>SELECT\n" +
            "\tso.origin_id,\n" +
            "\tso.origin_name\n" +
            "FROM\n" +
            "\tsys_origin so\n" +
            "WHERE\n" +
            "\tso.origin_id IN \n" +
            "<foreach item='item' index='index' collection='originList' open='(' separator=',' close=')'> " +
            " #{item} " +
            "</foreach>" +
            "</script>")
    List<HashMap<String,String>> getOriginsByOriginSet(@Param("originList") Set finalOriginSet);
}
