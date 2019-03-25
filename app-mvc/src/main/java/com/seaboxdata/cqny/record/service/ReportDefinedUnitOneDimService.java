package com.seaboxdata.cqny.record.service;

import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.entity.onedim.UnitDefined;
import com.webapp.support.page.PageResult;

import java.util.List;
import java.util.Map;

public interface ReportDefinedUnitOneDimService {

    PageResult pagerOnedimList(Integer unitId, Integer currPage, Integer pageSize);

    void addSaveOnedim(SimpleColumDefined simpleColumDefined);

    List<UnitDefined> getUnitsByOrigin(String originId);

    List<SimpleColumDefined> getColumByUnit(String unitId);

    void deleteOneDim(String columId);

    SimpleColumDefined getOnedimColumn(String columId);

    void editSaveOnedim(SimpleColumDefined simpleColumDefined);

    PageResult pagerOnedimListDynamic(Integer currPage, Integer pageSize, Integer unitId, String group_id);

    List<Map> getGroupByUnit(String unitId);

    void editSaveOnedimBat(Map<String, List<SimpleColumDefined>> maps);

    void deleteOneDimDynamic(Integer unitId, String group_id);
}
