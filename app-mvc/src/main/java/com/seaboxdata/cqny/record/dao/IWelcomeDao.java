package com.seaboxdata.cqny.record.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.ReportCustomer;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IWelcomeDao {

    @Select("<script>" +
            "select reportCust.*,so.origin_name as report_origin_name from " +
            "(select * from report_customer where report_origin=#{self_origin} and report_status=#{normal_status} " +
            " <if test=\"submit_status != null and originParams!=null \">  " +
            "union " +
            "select * from report_customer where report_origin in " +
            " <foreach item='item' index='index' collection='originParams' open='(' separator=',' close=')'> "+
            " #{item}" +
            " </foreach> " +
            "and report_status=#{submit_status} " +
            "</if>"+
            " <if test=\"review_status != null and originParams!=null\">  " +
            "union " +
            "select * from report_customer where report_origin in " +
            " <foreach item='item' index='index' collection='originParams' open='(' separator=',' close=')'> "+
            " #{item}" +
            " </foreach> and report_status = #{review_status}" +
            "</if>) reportCust,sys_origin so where reportCust.report_origin=so.origin_id " +
            "</script>")
    Page<ReportCustomer> jobList(
            @Param("currPage") Integer currPage,
            @Param("pageSize") Integer pageSize,
            @Param("self_origin") Integer self_origin,
            @Param("normal_status") String normal_status,
            @Param("originParams") List<Integer> originParams,
            @Param("submit_status") String submit_status,
            @Param("review_status") String review_status);

    @Select("<script>select count(0) report_status_sum,report_status from report_customer " +
            "where " +
            "report_origin in " +
            " <foreach item='item' index='index' collection='originParams' open='(' separator=',' close=')'> "+
            " #{item}" +
            " </foreach> " +
            "group by report_status</script>")
    List<Map<String, Integer>> getReportSumInfo(@Param("originParams") List<Integer> originParams);
}
