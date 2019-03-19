package com.seaboxdata.cqny.record.service;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.onedim.ColumDefined;
import com.seaboxdata.cqny.record.entity.onedim.UnitDefined;
import com.webapp.support.page.PageResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ReportDefinedUnitOneDimService {

    PageResult pagerOnedimList(Integer unitId, Integer currPage, Integer pageSize);

    void addSaveOnedim(ColumDefined columDefined);

    List<UnitDefined> getUnitsByOrigin(String originId);

    List<ColumDefined> getColumByUnit(String unitId);
}
