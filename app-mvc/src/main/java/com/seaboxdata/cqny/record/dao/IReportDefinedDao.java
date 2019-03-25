package com.seaboxdata.cqny.record.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.ReportDefined;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

public interface IReportDefinedDao {

    @Select("select * from report_defined")
    Page<Map<String, Object>> pageReportDefined(@Param("currPage") int currPage, @Param("pageSize") int pageSize);


    @Select("select * from report_defined where defined_id=#{definedId}")
    ReportDefined getReportDefinedById(Integer definedId);
}
