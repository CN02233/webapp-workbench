package com.seaboxdata.cqny.record.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.ReportInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface IReportApproveDao {


    @Select("<script>" +
            "SELECT distinct " +
            "ri.report_id as reportId," +
            "ri.template_id as reportTemplateId," +
            "ri.report_name as reportName," +
            "ri.report_status as reportStatus," +
            "ri.report_lock_user as lockedUser," +
            "ri.report_creater as reportCreate, " +
            "(select user_name from user where user_id = ri.report_creater) as reportCreateName,"+
            "ri.report_create_time as reportCreateDate ," +
            "ri.review_user as reviewUser,"+
            "ri.confirm_user as confirmUser,"+
            "(select user_name from user where user_id = ri.review_user) as reviewUserName,"+
            "(select user_name from user where user_id = ri.confirm_user) as confirmUserName "+
            "from report_infos ri inner join origin_template_assign ota on " +
            "ri.template_id = ota.template_id and ri.report_status = #{status}" +
            "and ota.origin_id in " +
            "<foreach item='item' index='index' collection='originList' open='(' separator=',' close=')'> " +
            " #{item} " +
            "</foreach>" +
            "</script>")
//    @Results(value = { @Result(column = "user_name", property = "username") })
    Page<ReportInfo> getApproveList(@Param("currPage") Integer currPage,
                                    @Param("pageSize") Integer pageSize,
                                    @Param("status") String status,
                                    @Param("originList") List originList);

}
