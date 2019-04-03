package com.seaboxdata.cqny.reportunit.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.reportunit.entity.StatementsEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    void addReportStatements(StatementsEntity reportDefined);

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
    void updateReportStatements(StatementsEntity reportDefined);

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
    Page<StatementsEntity> listReportStatements(@Param("currPage") int currPage, @Param("pageSize") int pageSize);

    @Select("SELECT\n" +
            "\trsi.create_date,\n" +
            "\trsi.create_user,\n" +
            "\trsi.origin_id,\n" +
            "\trsi.defined_id,\n" +
            "\trsi.defined_name,\n" +
            "\trsi.`status`\n" +
            "FROM\n" +
            "\treport_defined rsi\n" +
            "WHERE\n" +
            "\trsi.origin_id IN (\n" +
            "\t\tSELECT\n" +
            "\t\t\toos.origin_id\n" +
            "\t\tFROM\n" +
            "\t\t\tUSER u,\n" +
            "\t\t\tuser_organizations_assign uos,\n" +
            "\t\t\torganization_origin_assign oos\n" +
            "\t\tWHERE\n" +
            "\t\t\tu.user_id = uos.user_id\n" +
            "\t\tAND oos.organization_id = uos.organization_id\n" +
            "\t\tAND u.user_id = #{userId}\n" +
            "\t)")
    Page<StatementsEntity> listReportStatementsByUser(@Param("currPage")int currPage,@Param("pageSize") int pageSize,@Param("userId") int user_id);


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
}
