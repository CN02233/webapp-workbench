package com.seaboxdata.cqny.reportunit.service.impl;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.config.UnitDefinedType;
import com.seaboxdata.cqny.record.service.ReportDefinedUnitMultDimService;
import com.seaboxdata.cqny.record.service.ReportDefinedUnitOneDimService;
import com.seaboxdata.cqny.reportunit.dao.IReportUnitDao;
import com.seaboxdata.cqny.reportunit.entity.UnitEntity;
import com.seaboxdata.cqny.reportunit.service.ReportUnitService;
import com.webapp.support.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("reportUnit")
public class ReportUnitServiceImp implements ReportUnitService {

    @Autowired
    private IReportUnitDao reportUnitDao;

    @Autowired
    private ReportDefinedUnitOneDimService reportDefinedUnitOneDimService;
    @Autowired
    private ReportDefinedUnitMultDimService reportDefinedUnitMultDimService;

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

    @Override
    public UnitEntity getReportUnit(String unitId) {
        return reportUnitDao.getReportUnit(unitId);
    }

    @Override
    public List getDefinedColums(String unitId, String unitType){
        Integer unitTypeInt = new Integer(unitType);
        List columsDatas = null;
        if(UnitDefinedType.ONEDIMSTATIC.compareWith(unitTypeInt)){//一维静态
            columsDatas = reportDefinedUnitOneDimService.getColumByUnit(unitId);
        }else if(UnitDefinedType.ONEDIMDYNAMIC.compareWith(unitTypeInt)){//一维动态
            columsDatas = reportDefinedUnitOneDimService.getColumByUnit(unitId);
        }else if(UnitDefinedType.MANYDIMSTATIC.compareWith(unitTypeInt)){//多维静态
            columsDatas = reportDefinedUnitMultDimService.getColumByUnit(unitId);
        }else if(UnitDefinedType.MANYDIMTREE.compareWith(unitTypeInt)){//多维动态树
            columsDatas = reportDefinedUnitOneDimService.getColumByUnit(unitId);
        }else{

        }
        return columsDatas;
    }
}
