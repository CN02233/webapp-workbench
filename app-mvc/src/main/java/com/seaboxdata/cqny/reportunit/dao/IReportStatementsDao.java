package com.seaboxdata.cqny.reportunit.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.Origin;
import com.seaboxdata.cqny.reportunit.entity.StatementsEntity;
import com.seaboxdata.cqny.reportunit.entity.UnitEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IReportStatementsDao {

    @Insert("INSERT INTO report_statements_info (\n" +
            "\tstatements_name,\n" +
            "\tcreate_time,\n" +
            "\tcreate_user,\n" +
            "\torigin_id,\n" +
            "\tstatus\n" +
            ")\n" +
            "VALUES\n" +
            "\t(\n" +
            "\t#{ statements_name },sysdate(),#{ create_user },#{ origin_id },#{ status }\n" +
            "\t)")
    void addReportStatements(StatementsEntity reportStatements);

    @Delete("delete from report_statements_info where statements_id = #{statementsId}")
    void deleteById(@Param("statementsId") String statements_id);

    @Update("<script>update report_statements_info <set>"
            +"<if test='origin_id!=null'>"
            +"origin_id=#{origin_id} ,"
            +"</if>"
            +"<if test='statements_name!=null'>"
            +"statements_name=#{statements_name} ,"
            +"</if>"
            +"<if test='status!=null'>"
            +"status=#{status} ,"
            +"</if>"
            +"<if test='create_user!=null'>"
            +"create_user=#{create_user} ,"
            +"</if>"
            +"create_time=sysdate() "
            +"</set>where statements_id = #{statements_id}</script>")
    void updateReportStatements(StatementsEntity reportStatements);

    @Select("SELECT\n" +
            "\ta.create_time,\n" +
            "\ta.`status`,\n" +
            "\ta.statements_id,\n" +
            "\ta.statements_name,\n" +
            "\tb.origin_name,\n" +
            "\tc.user_name,\n" +
            "\tb.create_user,\n" +
            "\ta.origin_id\n" +
            "FROM\n" +
            "\treport_statements_info a\n" +
            "LEFT JOIN sys_origin b ON a.origin_id = b.origin_id\n" +
            "LEFT JOIN `user` c ON a.create_user = c.user_id\n" +
            "WHERE\n" +
            "\t1 = 1")
    Page<StatementsEntity> listReportStatements(@Param("currPage") int currPage, @Param("pageSize") int pageSize);

    @Select("SELECT\n" +
            "\trsi.create_time,\n" +
            "\trsi.create_user,\n" +
            "\trsi.origin_id,\n" +
            "\trsi.statements_id,\n" +
            "\trsi.statements_name,\n" +
            "\trsi.`status`\n" +
            "FROM\n" +
            "\treport_statements_info rsi\n" +
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
            "\t\tAND oos.organiztion_id = uos.organization_id\n" +
            "\t\tAND u.user_id = #{userId}\n" +
            "\t)")
    Page<StatementsEntity> listReportStatementsByUser(@Param("currPage")int currPage,@Param("pageSize") int pageSize,@Param("userId") int user_id);

    @Select("SELECT" +
            " a.create_time," +
            " a.`status`," +
            " a.statements_id," +
            " a.statements_name," +
            " b.origin_name," +
            " c.user_name," +
            " b.create_user," +
            " a.origin_id" +
            " FROM" +
            " report_statements_info a" +
            " LEFT JOIN sys_origin b ON a.origin_id = b.origin_id" +
            " LEFT JOIN `user` c ON a.create_user = c.user_id" +
            " WHERE" +
            " statements_id = #{definedId}")
    StatementsEntity getReportDefinedById(Integer definedId);

    @Select("select so.* from report_defined_origin_assign rdoa, sys_origin so where rdoa.defined_id=#{reportDefindId}" +
            " and so.origin_id = rdoa.origin_id")
    List<Origin> getOriginsByReportDefind(String reportDefindId);
}
