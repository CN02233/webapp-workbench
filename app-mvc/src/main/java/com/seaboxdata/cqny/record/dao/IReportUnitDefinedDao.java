package com.seaboxdata.cqny.record.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.reportunit.entity.UnitEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface IReportUnitDefinedDao {

    @Select("select * from report_defined_unit where report_defined_id = #{reportDefinedId}")
    Page<Map<String, Object>> pagerReportUnitDefine(
            @Param("reportDefinedId") Integer reportDefinedId,
            @Param("currPage") int currPage, @Param("pageSize") int pageSize);

    @Select("select * from report_unit_info where report_defined_id = #{reportDefinedId}")
    List<UnitEntity> getUnitDefinedByReportDefindId(String reportDefinedId);
}
