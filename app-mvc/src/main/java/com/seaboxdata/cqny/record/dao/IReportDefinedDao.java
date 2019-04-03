package com.seaboxdata.cqny.record.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.ReportDefined;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface IReportDefinedDao {

    @Select("select * from report_defined")
    Page<Map<String, Object>> pageReportDefined(@Param("currPage") int currPage, @Param("pageSize") int pageSize);


    @Select("select * from report_defined where defined_id=#{definedId}")
    ReportDefined getReportDefinedById(Integer definedId);

    @Update("update report_defined set status=#{status} where defined_id = #{reportDefinedId}")
    void changeReportDefinedStatus(@Param("reportDefinedId") String reportDefinedId,@Param("status") Integer status);

    @Select("select origin_id from report_defined_origin_assign where defined_id=#{reportDefindId}")
    List<Integer> getOriginsByReportDefind(String reportDefindId);
}
