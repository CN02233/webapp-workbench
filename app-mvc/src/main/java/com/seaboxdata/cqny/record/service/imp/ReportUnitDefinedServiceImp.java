package com.seaboxdata.cqny.record.service.imp;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.config.UnitDefinedType;
import com.seaboxdata.cqny.record.controller.ReportUnitDefinedController;
import com.seaboxdata.cqny.record.dao.IReportDefinedDao;
import com.seaboxdata.cqny.record.dao.IReportUnitDefinedDao;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.entity.onedim.UnitDefined;
import com.seaboxdata.cqny.record.service.ReportDefinedService;
import com.seaboxdata.cqny.record.service.ReportDefinedUnitOneDimService;
import com.seaboxdata.cqny.record.service.ReportDefinedUnitService;
import com.webapp.support.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("reportDefinedUnitService")
public class ReportUnitDefinedServiceImp implements ReportDefinedUnitService {

    @Autowired
    private IReportUnitDefinedDao reportUnitDefinedDao;

    @Autowired
    private ReportDefinedUnitOneDimService reportDefinedUnitOneDimService;

    @Override
    public PageResult pagerReportUnitDefinedList(Integer currPage, Integer pageSize,Integer reportDefinedId) {
        Page<Map<String, Object>> pager = reportUnitDefinedDao.pagerReportUnitDefine(reportDefinedId,currPage, pageSize);
        PageResult pageResult = PageResult.pageHelperList2PageResult(pager);
        return pageResult;
    }

    @Override
    public List getDefinedColums(String unitId,String unitType){
        Integer unitTypeInt = new Integer(unitType);
        if(UnitDefinedType.ONEDIMSTATIC.compareWith(unitTypeInt)){//一维静态
            List<SimpleColumDefined> columsDatas = reportDefinedUnitOneDimService.getColumByUnit(unitId);
            return columsDatas;
        }else if(UnitDefinedType.ONEDIMDYNAMIC.compareWith(unitTypeInt)){//一维动态

        }else if(UnitDefinedType.MANYDIMSTATIC.compareWith(unitTypeInt)){//多维静态

        }else if(UnitDefinedType.MANYDIMTREE.compareWith(unitTypeInt)){//多维动态树

        }else{

        }
        return null;
    }
}
