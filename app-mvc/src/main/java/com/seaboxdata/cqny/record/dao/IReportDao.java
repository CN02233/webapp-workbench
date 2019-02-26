package com.seaboxdata.cqny.record.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.ReportInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface IReportDao {

    static final String select_column = "SELECT " +
            "ri.report_id as reportId," +
            "ri.report_name as reportName," +
            "ri.report_path as reportPath, " +
            "ri.report_creater as reportCreate, " +
            "ri.report_create_time as reportCreateDate, " +
            "ri.template_name as reportTemplateName "+
            "FROM report_infos ri ";

    @Select(select_column)
    @Options(useCache = false)
    Page<ReportInfo> reportList(@Param("currPage") int currPage, @Param("pageSize") int pageSize,@Param("user_id")int  user_id);

    @Insert("insert into report_infos (report_name,report_path,report_creater,report_create_time,template_name) values " +
            "(#{reportName},#{reportPath},#{reportCreate},#{reportCreateDate},#{reportTemplateName})")
    @Options(useGeneratedKeys = true, keyProperty = "reportId", keyColumn = "report_id")
    int createReport(ReportInfo reportInfo);

    @Select(select_column+ " where ri.report_id=#{reportIdOrName}")
    ReportInfo getReportInfoById(String reportIdOrName);
}
