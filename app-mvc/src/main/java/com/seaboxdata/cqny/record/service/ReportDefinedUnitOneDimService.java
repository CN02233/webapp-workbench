package com.seaboxdata.cqny.record.service;

import com.seaboxdata.cqny.record.entity.onedim.GridColumDefined;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.entity.UnitDefined;
import com.webapp.support.page.PageResult;

import java.util.List;
import java.util.Map;

public interface ReportDefinedUnitOneDimService {

    PageResult pagerOnedimList(Integer unitId, Integer currPage, Integer pageSize);

    void addSaveOnedim(SimpleColumDefined simpleColumDefined);

    List<UnitDefined> getUnitsByOrigin(String originId);

    List getColumByUnit(String unitId);

    void deleteOneDim(String columId);

    SimpleColumDefined getOnedimColumn(String columId);

    void editSaveOnedim(SimpleColumDefined simpleColumDefined);

    PageResult pagerOnedimListDynamic(Integer currPage, Integer pageSize, Integer unitId, Map<String,Object> map);

    List<Map> getGroupByUnit(String unitId);

    void editSaveOnedimDynamic(SimpleColumDefined group, Map<String, List<SimpleColumDefined>> maps);

    void deleteOneDimDynamic(Integer unitId, String group_id);

    PageResult pagerMultdimListStatic(Integer currPage, Integer pageSize, Integer unitId, Map<String,Object> map);
}
