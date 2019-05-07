package com.seaboxdata.cqny.record.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.onedim.GridColumDefined;
import com.seaboxdata.cqny.record.entity.UnitDefined;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

public interface IReportDefinedUnitMultDimDao {

    @Select("<script>select * from "
            +" (select a.*,b.colum_name,b.colum_name_cn,b.colum_point,b.colum_desc,c.dim_name,c.dim_name_cn from report_defined_unit_multdim a" +
            " left join report_defined_unit_multdim_col b on a.colum_id=b.colum_id" +
            " left join report_defined_unit_multdim_dim c on a.dim_id=c.dim_id" +
            " where a.unit_id=#{unitId}" +
            "<if test=\"columId!=null\"> and a.colum_id = #{columId}</if>"+
            ") x order by colum_id, dim_id</script>")
    Page<Map<String, Object>> pagerMultdimList(@Param("unitId") Integer unitId, @Param("columId") Integer columId, @Param("currPage") Integer currPage, @Param("pageSize") Integer pageSize);

    @Select("select * from report_defined_unit_multdim_dim where unit_id=#{unitId}")
    Page<Map<String, Object>> pagerDimList(@Param("unitId") Integer unitId, @Param("currPage") Integer currPage, @Param("pageSize") Integer pageSize);

    @Insert("insert into report_defined_unit_multdim " +
            "(unit_id,colum_id,dim_id,min_value,max_value ," +
            "colum_formula,colum_formula_desc,colum_type,need_remember) values " +
            "(#{unit_id},#{colum_id},#{dim_id},#{min_value},#{max_value}," +
            "#{colum_formula},#{colum_formula_desc},#{colum_type},#{need_remember})")
    void addSaveMultdim(GridColumDefined simpleColumDefined);
    @Insert("insert into report_defined_unit_multdim_col(colum_name,colum_name_cn,unit_id,colum_point,colum_desc) values " +
            "(#{colum_name},#{colum_name_cn},#{unit_id},#{colum_point},#{colum_desc})")
    @Options(useGeneratedKeys = true, keyProperty = "colum_id", keyColumn = "colum_id")
    void addSaveMultdim_col(GridColumDefined simpleColumDefined);

    @Insert("insert into report_defined_unit_multdim_dim(dim_name,dim_name_cn,unit_id) values " +
            "(#{dim_name},#{dim_name_cn},#{unit_id})")
    @Options(useGeneratedKeys = true, keyProperty = "dim_id", keyColumn = "dim_id")
    void addSaveMultdim_dim(GridColumDefined simpleColumDefined);

    @Update("update report_defined_unit_multdim set colum_type=#{colum_type},min_value=#{min_value},max_value=#{max_value} ," +
            "colum_formula=#{colum_formula},colum_formula_desc=#{colum_formula_desc},need_remember=#{need_remember}" +
            " where unit_id=#{unit_id} and colum_id=#{colum_id} and dim_id=#{dim_id}")
    void editSaveMultdim(GridColumDefined simpleColumDefined);

    @Update("update report_defined_unit_multdim_col set colum_name=#{colum_name},colum_name_cn=#{colum_name_cn},unit_id=#{unit_id},colum_point=#{colum_point},colum_desc=#{colum_desc} where colum_id=#{colum_id}")
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

    @Select("select * from report_unit_info where report_defined_id in (select report_defined_id from report_unit_info where unit_id=#{originId})")
    List<UnitDefined> getUnitByOrigin(String originId);

    @Select("select distinct colum_id, colum_name, colum_name_cn, group_id dim_id, group_name dim_name, unit_id, colum_type, min_value, max_value, colum_point, colum_formula, colum_formula_desc, colum_desc,need_remember from report_defined_unit_onedim where unit_id = #{unitId}")
    List<GridColumDefined> getOneColumByUnit(String unitId);
    @Select("<script>select a.*,aa.colum_name,aa.colum_name_cn,aa.colum_point,aa.colum_desc,'1' colum_meta_type from report_defined_unit_multdim a left join report_defined_unit_multdim_col aa on a.colum_id=aa.colum_id where a.unit_id=#{unitId}" +
            "union select unit_id,colum_id,'','','','','','','N','','',colum_name,colum_name_cn,colum_point, colum_desc,'2' colum_meta_type from report_defined_unit_multdim_col b where b.unit_id=#{unitId}" +
            "union select unit_id,'',dim_id,'','','','','','N','','',dim_name,dim_name_cn,'','','3' colum_meta_type from report_defined_unit_multdim_dim c where c.unit_id=#{unitId}</script>")
    List<GridColumDefined> getColumByUnit(String unitId);

    @Select(
        "SELECT " +
        "rdum.colum_id ," +
        "rdum.dim_id ," +
        "rdumc.colum_name ," +
        "rdumc.colum_name_cn ," +
        "rdumd.dim_name ," +
        "rdumd.dim_name_cn ," +
        "rdum.unit_id ," +
        "rdum.colum_type ," +
        "rdum.min_value ," +
        "rdum.max_value ," +
        "rdum.colum_formula ," +
        "rdum.colum_formula_desc ," +
        "rdumc.colum_point ," +
        "rdumc.colum_desc ," +
        "rdum.need_remember " +
        "FROM " +
        "report_defined_unit_multdim rdum,report_defined_unit_multdim_col rdumc," +
        "report_defined_unit_multdim_dim rdumd where rdum.unit_id=#{unitId} and" +
        " rdum.colum_id = rdumc.colum_id and rdum.dim_id = rdumd.dim_id")
    List<GridColumDefined> getGridColumDefindsByUNit(String unitId);

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
            +"max(colum_point) colum_point, max(colum_desc) colum_desc,max(colum_formula) colum_formula, max(colum_formula_desc) colum_formula_desc," +
            "group_concat(dim_name_cn ORDER BY dim_id) dim_name_cn"
            +" from (select a.*,b.colum_name_cn,c.dim_name_cn,b.colum_point,b.colum_desc from report_defined_unit_multdim a" +
            " left join report_defined_unit_multdim_col b on a.colum_id=b.colum_id" +
            " left join report_defined_unit_multdim_dim c on a.dim_id=c.dim_id where a.unit_id=#{unitId}" +
            ") x group by colum_id order by colum_id</script>")
    Page<Map<String, Object>> pagerMultdimListStatic(@Param("currPage") Integer currPage, @Param("pageSize") Integer pageSize, @Param("unitId") Integer unitId, @Param("map") Map<String, Object> map);


    @Select("select " +
            "rdum.*,rdumc.colum_name,rdumc.colum_name_cn," +
            "rdumd.dim_name,rdumd.dim_name_cn "+
            " from report_defined_unit_multdim rdum ,report_defined_unit_multdim_col rdumc ,report_defined_unit_multdim_dim rdumd" +
            " where rdum.unit_id = #{unit_id} and rdum.colum_id=rdumc.colum_id and rdum.dim_id = rdumd.dim_id ")
    List<GridColumDefined> getMultDefindByUnit(String unitId);
}
