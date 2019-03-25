package com.seaboxdata.cqny.reportunit.service.impl;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.reportunit.dao.IReportUnitDao;
import com.seaboxdata.cqny.reportunit.entity.UnitEntity;
import com.seaboxdata.cqny.reportunit.service.ReportUnitService;
import com.webapp.support.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("reportUnit")
public class ReportUnitServiceImp implements ReportUnitService {

    @Autowired
    private IReportUnitDao reportUnitDao;


    @Override
    public PageResult listReportUnit(int currPage, int pageSize) {
        Page<UnitEntity> reportUnitPage = reportUnitDao.listReportUnit(currPage, pageSize);
        PageResult pageResult = PageResult.pageHelperList2PageResult(reportUnitPage);
        return pageResult;
    }

    @Override
    public void addReportUnit(UnitEntity reportUnit) {
        if(reportUnit.getUnit_id()!=null){
            reportUnitDao.updateReportUnit(reportUnit);
        }else{
            reportUnitDao.addReportUnit(reportUnit);
        }
    }

    @Override
    public void deleteById(String unitId) {
        reportUnitDao.deleteById(unitId);
    }
}
