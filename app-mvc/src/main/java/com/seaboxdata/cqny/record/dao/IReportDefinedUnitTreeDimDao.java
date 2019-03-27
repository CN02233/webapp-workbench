package com.seaboxdata.cqny.record.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface IReportDefinedUnitTreeDimDao {

//    @Select("select " +
//            "colum_id,colum_name,colum_name_cn,group_id,group_name,unit_id," +
//            "colum_data_type,min_value,max_value,colum_formula,colum_formula_desc," +
//            "parent_id,parent_name,colum_type,colum_desc " +
//            " from report_defined_unit_onedim where unit_id = #{unitId}")
//    Page<SimpleColumDefined> pagerTreedimList(
//            @Param("unitId") Integer unitId,
//            @Param("currPage")  Integer currPage,
//            @Param("pageSize")  Integer pageSize);
}
