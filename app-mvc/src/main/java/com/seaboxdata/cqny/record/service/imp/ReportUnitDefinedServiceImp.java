package com.seaboxdata.cqny.record.service.imp;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.controller.ReportUnitDefinedController;
import com.seaboxdata.cqny.record.dao.IReportDefinedDao;
import com.seaboxdata.cqny.record.dao.IReportUnitDefinedDao;
import com.seaboxdata.cqny.record.service.ReportDefinedService;
import com.seaboxdata.cqny.record.service.ReportDefinedUnitService;
import com.webapp.support.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("reportDefinedUnitService")
public class ReportUnitDefinedServiceImp implements ReportDefinedUnitService {

    @Autowired
    private IReportUnitDefinedDao reportUnitDefinedDao;


    @Override
    public PageResult pagerReportUnitDefinedList(Integer currPage, Integer pageSize,Integer reportDefinedId) {
        Page<Map<String, Object>> pager = reportUnitDefinedDao.pagerReportUnitDefine(reportDefinedId,currPage, pageSize);
        PageResult pageResult = PageResult.pageHelperList2PageResult(pager);
        return pageResult;
    }
}
