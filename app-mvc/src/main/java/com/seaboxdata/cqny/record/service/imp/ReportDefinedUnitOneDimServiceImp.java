package com.seaboxdata.cqny.record.service.imp;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.dao.IReportDefinedUnitOneDimDao;
import com.seaboxdata.cqny.record.entity.onedim.ColumDefined;
import com.seaboxdata.cqny.record.service.ReportDefinedUnitOneDimService;
import com.webapp.support.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("reportDefinedUnitOneDimService")
public class ReportDefinedUnitOneDimServiceImp implements ReportDefinedUnitOneDimService {

    @Autowired
    private IReportDefinedUnitOneDimDao reportDefinedUnitOneDimDao;

    @Override
    public PageResult pagerOnedimList(Integer unitId, Integer currPage, Integer pageSize) {
        Page<Map<String, Object>> pageData = reportDefinedUnitOneDimDao.pagerOnedimList(unitId, currPage, pageSize);
        PageResult pageResult = PageResult.pageHelperList2PageResult(pageData);
        return pageResult;    }

    @Override
    public void addSaveOnedim(ColumDefined columDefined) {
        reportDefinedUnitOneDimDao.addSaveOnedim(columDefined);
    }
}