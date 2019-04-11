package com.seaboxdata.cqny.record.service;

import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.entity.UnitDefined;
import com.webapp.support.page.PageResult;

import java.util.List;
import java.util.Map;

public interface ReportDefinedUnitTreeDimService {
    PageResult pagerTreedimList(Integer unitId, Integer currPage, Integer pageSize);

    void addSaveTreedim(SimpleColumDefined simpleColumDefined);

    void editSaveTreedim(SimpleColumDefined simpleColumDefined);

    SimpleColumDefined getTreedimColumn(String columId);

    void deleteTreeDim(String columId);

    List<UnitDefined> getUnitsByOrigin(String originId);

    List<Map> getGroupByUnit(String unitId);

    List<SimpleColumDefined> getColumByUnit(String unitId);
}
