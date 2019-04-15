package com.seaboxdata.cqny.record.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.entity.UnitDefined;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

public interface IReportDefinedUnitOneDimDao {

    @Select("select * from report_defined_unit_onedim where unit_id = #{unitId}")
    Page<Map<String, Object>> pagerOnedimList(@Param("unitId") Integer unitId, @Param("currPage") Integer currPage, @Param("pageSize") Integer pageSize);



    @Insert("insert into report_defined_unit_onedim " +
            "(colum_name,colum_name_cn,group_id," +
            "group_name,unit_id,min_value,max_value ,colum_formula,colum_formula_desc," +
            "parent_id,parent_name,colum_type,colum_desc,colum_point,need_remember,default_value) values " +
            "(#{colum_name},#{colum_name_cn},#{group_id},#{group_name},#{unit_id}," +
            "#{min_value},#{max_value},#{colum_formula},#{colum_formula_desc}," +
            "#{parent_id},#{parent_name},#{colum_type},#{colum_desc},#{colum_point},#{need_remember},#{default_value})")
    @Options(useGeneratedKeys = true, keyProperty = "colum_id", keyColumn = "colum_id")
    void addSaveOnedim(SimpleColumDefined simpleColumDefined);

    @Select("select * from report_unit_info")
    List<UnitDefined> getUnitByOrigin(String originId);

    @Select("select * from report_defined_unit_onedim where unit_id = #{unitId}")
    List<SimpleColumDefined> getColumByUnit(String unitId);

    @Delete("delete from report_defined_unit_onedim where colum_id=#{columnId}")
    void deleteOneDim(String columId);

    @Select("select * from report_defined_unit_onedim where colum_id=#{columnId}")
    SimpleColumDefined getOnedimColumn(String columId);

    @Update("update report_defined_unit_onedim set colum_name=#{colum_name},colum_name_cn=#{colum_name_cn}," +
            "group_id=#{group_id},group_name=#{group_name}," +
            "min_value=#{min_value},max_value=#{max_value} ," +
            "parent_id=#{parent_id},parent_name=#{parent_name},colum_type=#{colum_type},colum_desc=#{colum_desc} ," +
            "colum_formula=#{colum_formula},colum_formula_desc=#{colum_formula_desc},colum_point=#{colum_point}," +
            "need_remember=#{need_remember},default_value=#{default_value} where colum_id=#{colum_id}")
    void editSaveOnedim(SimpleColumDefined simpleColumDefined);

    @Select("<script>select * from report_defined_unit_onedim where unit_id = #{unitId}"
            +"<if test=\"map.group_id!='' and map.group_id!=null and map.group_id!='null'\"> and group_id = #{map.group_id}</if>"
            +"<if test=\"map.inc_group_id=='' or map.inc_group_id==null\"> and group_id is not null</if>"
            +"<if test=\"map.inc_group_id!='' and map.inc_group_id!=null and map.inc_group_id!='null'\"> and (group_id = #{map.inc_group_id} or colum_id = #{map.inc_group_id})</if>"
            +" order by group_id,colum_id</script>")
    Page<Map<String, Object>> pagerOnedimListDynamic(@Param("currPage") Integer currPage, @Param("pageSize") Integer pageSize, @Param("unitId") Integer unitId, @Param("map") Map<String,Object> map);

    @Select("select * from report_defined_unit where defined_id=#{originId}")
    List<UnitDefined> getDefinedUnitByOrigin(String originId);

    @Select("select colum_id group_id,colum_name_cn group_name from report_defined_unit_onedim where unit_id = #{unitId} and group_id is null")
    List<Map> getGroupByUnit(String unitId);

    @Select("<script>select * from ( select a.*,b.dim_name from report_defined_unit_onedim a "
            +" left join (select group_concat(`colum_name_cn`) dim_name, unit_id uid,group_id gid from report_defined_unit_onedim where unit_id=#{unitId} and colum_type='2' group by group_id) b"
            +" on a.unit_id=b.uid and a.group_id=b.gid where unit_id=#{unitId} and colum_type='1' and group_id is not null"
            +"<if test=\"map.group_id!='' and map.group_id!=null and map.group_id!='null'\"> and group_id = #{map.group_id}</if>"
            +") x order by group_id,colum_id</script>")
    Page<Map<String, Object>> pagerMultdimListStatic(@Param("currPage") Integer currPage, @Param("pageSize") Integer pageSize, @Param("unitId") Integer unitId, @Param("map") Map<String,Object> map);


    @Select("select * from report_defined_unit_onedim where colum_formula like CONCAT('%','${fomularParamName}','%' )")
    List<SimpleColumDefined> checkSimpleDefinedFomulars(@Param("fomularParamName") String fomularParamName);
}
