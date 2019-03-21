package com.seaboxdata.cqny.record.service;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.onedim.ColumDefined;
import com.seaboxdata.cqny.record.entity.onedim.UnitDefined;
import com.webapp.support.page.PageResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ReportDefinedUnitOneDimService {

    PageResult pagerOnedimList(Integer unitId, Integer currPage, Integer pageSize);

    void addSaveOnedim(ColumDefined columDefined);

    List<UnitDefined> getUnitsByOrigin(String originId);

    List<ColumDefined> getColumByUnit(String unitId);

    void deleteOneDim(String columId);

    ColumDefined getOnedimColumn(String columId);

    void editSaveOnedim(ColumDefined columDefined);

    PageResult pagerOnedimListDynamic(Integer currPage, Integer pageSize, Integer unitId, String group_id);

    List<Map> getGroupByUnit(String unitId);

    void editSaveOnedimBat(Map<String, List<ColumDefined>> maps);

    void deleteOneDimDynamic(Integer unitId, String group_id);
}
