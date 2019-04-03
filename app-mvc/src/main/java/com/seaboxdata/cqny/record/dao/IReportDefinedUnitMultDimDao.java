package com.seaboxdata.cqny.record.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.onedim.GridColumDefined;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.entity.onedim.UnitDefined;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

public interface IReportDefinedUnitMultDimDao {

    @Select("<script>select * from "
            +" (select a.*,b.colum_name,b.colum_name_cn,c.dim_name,c.dim_name_cn from report_defined_unit_multdim a" +
            " left join report_defined_unit_multdim_col b on a.colum_id=b.colum_id" +
            " left join report_defined_unit_multdim_dim c on a.dim_id=c.dim_id" +
            " where a.unit_id=#{unitId}" +
            "<if test=\"columId!=null\"> and a.colum_id = #{columId}</if>"+
            ") x order by colum_id</script>")
    Page<Map<String, Object>> pagerMultdimList(@Param("unitId") Integer unitId, @Param("columId") Integer columId, @Param("currPage") Integer currPage, @Param("pageSize") Integer pageSize);

    @Select("select * from report_defined_unit_multdim_dim where unit_id=#{unitId}")
    Page<Map<String, Object>> pagerDimList(@Param("unitId") Integer unitId, @Param("currPage") Integer currPage, @Param("pageSize") Integer pageSize);

    @Select("select count(*) from report_defined_unit_multdim_dim where unit_id=#{unitId} and dim_id=#{dimId}")
    int countDimList(@Param("unitId") Integer unitId, @Param("dimId") Integer dimId);


    @Insert("insert into report_defined_unit_multdim " +
            "(unit_id,colum_id,dim_id,min_value,max_value ," +
            "colum_formula,colum_formula_desc,colum_type) values " +
            "(#{unit_id},#{colum_id},#{dim_id},#{min_value},#{max_value}," +
            "#{colum_formula},#{colum_formula_desc},#{colum_type})")
    void addSaveMultdim(GridColumDefined simpleColumDefined);
    @Insert("insert into report_defined_unit_multdim_col(colum_name,colum_name_cn) values " +
            "(#{colum_name},#{colum_name_cn})")
    @Options(useGeneratedKeys = true, keyProperty = "colum_id", keyColumn = "colum_id")
    void addSaveMultdim_col(GridColumDefined simpleColumDefined);

    @Insert("insert into report_defined_unit_multdim_dim(dim_name,dim_name_cn,unit_id) values " +
            "(#{dim_name},#{dim_name_cn},#{unit_id})")
    @Options(useGeneratedKeys = true, keyProperty = "dim_id", keyColumn = "dim_id")
    void addSaveMultdim_dim(GridColumDefined simpleColumDefined);

    @Update("update report_defined_unit_multdim set colum_type=#{colum_type},min_value=#{min_value},max_value=#{max_value} ," +
            "colum_formula=#{colum_formula},colum_formula_desc=#{colum_formula_desc}" +
            " where unit_id=#{unit_id} and colum_id=#{colum_id} and dim_id=#{dim_id}")
    void editSaveMultdim(GridColumDefined simpleColumDefined);

    @Update("update report_defined_unit_multdim_col set colum_name=#{colum_name},colum_name_cn=#{colum_name_cn} where colum_id=#{colum_id}")
    void editSaveMultdim_col(GridColumDefined simpleColumDefined);

    @Update("update report_defined_unit_multdim_dim set dim_name=#{dim_name},dim_name_cn=#{dim_name_cn},unit_id=#{unit_id} where dim_id=#{dim_id}")
    void editSaveMultdim_dim(GridColumDefined simpleColumDefined);

    @Delete("<script>delete from report_defined_unit_multdim where unit_id=#{unitId}" +
            "<if test=\"columId!=null\"> and colum_id=#{columId}</if>"+
            "<if test=\"dimId!=null\"> and dim_id=#{dimId}</if>"+
            "</script>")
    void deleteMultDim(@Param("unitId") Integer unitId, @Param("columId") Integer columId,@Param("dimId") Integer dimId);

    @Delete("delete from report_defined_unit_multdim_dim where dim_id=#{dimId}")
    void deleteMultDim_dim(@Param("dimId") Integer dimId);

    @Delete("delete from report_defined_unit_multdim_col where colum_id=#{columId}")
    void deleteMultDim_col(@Param("columId") Integer columId);

    @Select("select * from report_defined_unit where report_defined_id=#{originId}")
    List<UnitDefined> getUnitByOrigin(String originId);

    @Select("<script>select a.*,'' colum_name, '' colum_name_cn,'1' colum_meta_type from report_defined_unit_multdim a where a.unit_id=#{unitId}" +
            "union select #{unitId},colum_id,'','','','','','',colum_name,colum_name_cn,'2' colum_meta_type from report_defined_unit_multdim_col b where b.colum_id in (select colum_id from report_defined_unit_multdim where unit_id=#{unitId})" +
            "union select unit_id,'',dim_id,'','','','','',dim_name,dim_name_cn,'3' colum_meta_type from report_defined_unit_multdim_dim c where c.unit_id=#{unitId}</script>")
    List<GridColumDefined> getColumByUnit(String unitId);

    @Select("select * from report_defined_unit_onedim where colum_id=#{columId}")
    GridColumDefined getOnedimColumn(@Param("columId") String columId);

    @Select("<script>select * from report_defined_unit_onedim where unit_id = #{unitId}"
            +"<if test=\"map.group_id!='' and map.group_id!=null and map.group_id!='null'\"> and group_id = #{map.group_id}</if>"
            +"<if test=\"map.inc_group_id=='' or map.inc_group_id==null\"> and group_id is not null</if>"
            +"<if test=\"map.inc_group_id!='' and map.inc_group_id!=null and map.inc_group_id!='null'\"> and (group_id = #{map.inc_group_id} or colum_id = #{map.inc_group_id})</if>"
            +" order by group_id,colum_id</script>")
    Page<Map<String, Object>> pagerOnedimListDynamic(@Param("currPage") Integer currPage, @Param("pageSize") Integer pageSize, @Param("unitId") Integer unitId, @Param("map") Map<String, Object> map);

    @Select("select colum_id group_id,colum_name_cn group_name from report_defined_unit_onedim where unit_id = #{unitId} and group_id is null")
    List<Map> getGroupByUnit(String unitId);

    @Select("<script>select colum_id,max(colum_name_cn) colum_name_cn, max(colum_type) colum_type, max(min_value) min_value, max(max_value) max_value, "
            +"max(colum_formula) colum_formula, max(colum_formula_desc) colum_formula_desc,group_concat(dim_name_cn ORDER BY dim_id) dim_name_cn"
            +" from (select a.*,b.colum_name_cn,c.dim_name_cn from report_defined_unit_multdim a" +
            " left join report_defined_unit_multdim_col b on a.colum_id=b.colum_id" +
            " left join report_defined_unit_multdim_dim c on a.dim_id=c.dim_id where a.unit_id=#{unitId}" +
            ") x group by colum_id order by colum_id</script>")
    Page<Map<String, Object>> pagerMultdimListStatic(@Param("currPage") Integer currPage, @Param("pageSize") Integer pageSize, @Param("unitId") Integer unitId, @Param("map") Map<String, Object> map);

}
