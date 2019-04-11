package com.seaboxdata.cqny.record.service.imp;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.dao.IReportDefinedUnitOneDimDao;
import com.seaboxdata.cqny.record.dao.IReportDefinedUnitTreeDimDao;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.entity.UnitDefined;
import com.seaboxdata.cqny.record.service.ReportDefinedUnitTreeDimService;
import com.webapp.support.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("reportDefinedUnitTreeDimService")
public class ReportDefinedUnitTreeDimServiceImp implements ReportDefinedUnitTreeDimService {

    @Autowired
    private IReportDefinedUnitOneDimDao reportDefinedUnitOneDimDao;

    @Autowired
    private IReportDefinedUnitTreeDimDao reportDefinedUnitTreeDimDao;

    @Override
    public PageResult pagerTreedimList(Integer unitId, Integer currPage, Integer pageSize) {
        Page<Map<String, Object>> pageData = reportDefinedUnitOneDimDao.pagerOnedimList(unitId, currPage, pageSize);
        PageResult pageResult = PageResult.pageHelperList2PageResult(pageData);
        return pageResult;
    }

    @Override
    public void addSaveTreedim(SimpleColumDefined simpleColumDefined) {

    }

    @Override
    public void editSaveTreedim(SimpleColumDefined simpleColumDefined) {

    }

    @Override
    public SimpleColumDefined getTreedimColumn(String columId) {
        return null;
    }

    @Override
    public void deleteTreeDim(String columId) {

    }

    @Override
    public List<UnitDefined> getUnitsByOrigin(String originId) {
        return null;
    }

    @Override
    public List<Map> getGroupByUnit(String unitId) {
        return null;
    }

    @Override
    public List<SimpleColumDefined> getColumByUnit(String unitId) {
        return null;
    }
}
