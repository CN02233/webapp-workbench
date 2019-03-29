package com.seaboxdata.cqny.record.service;

import com.seaboxdata.cqny.record.entity.onedim.GridColumDefined;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.entity.onedim.UnitDefined;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;

import java.util.List;
import java.util.Map;

public interface ReportDefinedUnitMultDimService {

    PageResult pagerMultdimList(Integer unitId,Integer columId, Integer currPage, Integer pageSize);

    PageResult pagerDimList(Integer unitId, Integer currPage, Integer pageSize);

    List<UnitDefined> getUnitsByOrigin(String originId);

    List<GridColumDefined> getColumByUnit(String unitId);

    GridColumDefined getOnedimColumn(String columId);

    PageResult pagerOnedimListDynamic(Integer currPage, Integer pageSize, Integer unitId, Map<String, Object> map);

    List<Map> getGroupByUnit(String unitId);

    void addSaveMultdim_dim(GridColumDefined maps);

    void editSaveMultdim_dim(GridColumDefined maps);

    JsonResult deleteSaveMultdim_dim(GridColumDefined maps);

    void editSaveMultdim(Map<String, List<GridColumDefined>> maps);

    void deleteMultdim(Integer unitId, Integer columId);

    PageResult pagerMultdimListStatic(Integer currPage, Integer pageSize, Integer unitId, Map<String, Object> map);
}
