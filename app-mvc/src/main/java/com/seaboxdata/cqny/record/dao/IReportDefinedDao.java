package com.seaboxdata.cqny.record.dao;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

public interface IReportDefinedDao {

    @Select("select * from report_defined")
    Page<Map<String, Object>> pageReportDefined(@Param("currPage") int currPage, @Param("pageSize") int pageSize);
}
