package com.seaboxdata.cqny.record.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.onedim.ColumDefined;
import com.seaboxdata.cqny.record.entity.onedim.UnitDefined;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

public interface IReportDefinedUnitOneDimDao {

    @Select("select * from report_defined_unit_onedim where unit_id = #{unitId}")
    Page<Map<String, Object>> pagerOnedimList(@Param("unitId") Integer unitId, @Param("currPage") Integer currPage, @Param("pageSize") Integer pageSize);



    @Insert("insert into report_defined_unit_onedim " +
            "(colum_name,colum_name_cn,group_id," +
            "group_name,unit_id,colum_data_type,min_value,max_value ,colum_formula,colum_formula_desc) values " +
            "(#{colum_name},#{colum_name_cn},#{group_id},#{group_name},#{unit_id},#{colum_data_type}," +
            "#{min_value},#{max_value},#{colum_formula},#{colum_formula_desc})")
    @Options(useGeneratedKeys = true, keyProperty = "colum_id", keyColumn = "colum_id")
    void addSaveOnedim(ColumDefined columDefined);

    @Select("select * from report_defined_unit")
    List<UnitDefined> getUnitByOrigin(String originId);

    @Select("select * from report_defined_unit_onedim where unit_id = #{unitId}")
    List<ColumDefined> getColumByUnit(String unitId);

    @Delete("delete from report_defined_unit_onedim where colum_id=#{columnId}")
    void deleteOneDim(String columId);

    @Select("select * from report_defined_unit_onedim where colum_id=#{columnId}")
    ColumDefined getOnedimColumn(String columId);

    @Update("update report_defined_unit_onedim set colum_name=#{colum_name},colum_name_cn=#{colum_name_cn}," +
            "group_id=#{group_id},group_name=#{group_name}," +
            "colum_data_type=#{colum_data_type},min_value=#{min_value},max_value=#{max_value} ," +
            "colum_formula=#{colum_formula},colum_formula_desc=#{colum_formula_desc} where colum_id=#{colum_id}")
    void editSaveOnedim(ColumDefined columDefined);
}