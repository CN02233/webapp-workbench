package com.seaboxdata.cqny.record.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.onedim.ColumDefined;
import com.seaboxdata.cqny.record.entity.onedim.UnitDefined;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface IReportDefinedUnitOneDimDao {

    @Select("select * from report_defined_unit_onedim where unit_id = #{unitId}")
    Page<Map<String, Object>> pagerOnedimList(@Param("unitId") Integer unitId, @Param("currPage") Integer currPage, @Param("pageSize") Integer pageSize);



    @Insert("insert into report_defined_unit_onedim " +
            "(colum_name,colum_name_cn,group_id," +
            "group_name,unit_id,colum_data_type,min_value,max_value ,colum_formula) values " +
            "(#{colum_name},#{colum_name_cn},#{group_id},#{group_name},#{unit_id},#{colum_data_type}," +
            "#{min_value},#{max_value},#{colum_formula})")
    @Options(useGeneratedKeys = true, keyProperty = "colum_id", keyColumn = "colum_id")
    void addSaveOnedim(ColumDefined columDefined);

    @Select("select * from report_defined_unit")
    List<UnitDefined> getUnitByOrigin(String originId);

    @Select("select * from report_defined_unit_onedim where unit_id = #{unitId}")
    List<ColumDefined> getColumByUnit(String unitId);
}
