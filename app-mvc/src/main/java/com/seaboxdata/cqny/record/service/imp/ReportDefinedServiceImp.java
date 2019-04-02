package com.seaboxdata.cqny.record.service.imp;

import com.alibaba.druid.sql.PagerUtils;
import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.dao.IReportDefinedDao;
import com.seaboxdata.cqny.record.entity.ExcelTemplate;
import com.seaboxdata.cqny.record.entity.ReportDefined;
import com.seaboxdata.cqny.record.entity.ReportDefinedStatus;
import com.seaboxdata.cqny.record.entity.onedim.UnitDefined;
import com.seaboxdata.cqny.record.service.ReportDefinedService;
import com.webapp.support.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("reportDefinedService")
public class ReportDefinedServiceImp implements ReportDefinedService {

    @Autowired
    private IReportDefinedDao reportDefinedDao;

    @Override
    public PageResult reportDefinedList(Integer currPage, Integer pageSize) {
        Page<Map<String,Object>> pageData = reportDefinedDao.pageReportDefined(currPage, pageSize);
        PageResult pageResult = PageResult.pageHelperList2PageResult(pageData);
        return pageResult;
    }

    @Override
    public ReportDefined getReportDefinedById(Integer definedId) {
        ReportDefined reportDefined = reportDefinedDao.getReportDefinedById(definedId);
        return reportDefined;
    }

    @Override
    public List<UnitDefined> getUnitDefinedsByRepotDefinedId(Integer definedId) {
        return null;
    }

    @Override
    public void activeReportDefined(String definedId){
        reportDefinedDao.changeReportDefinedStatus(definedId, ReportDefinedStatus.SUBMIT.value());
    }
}
