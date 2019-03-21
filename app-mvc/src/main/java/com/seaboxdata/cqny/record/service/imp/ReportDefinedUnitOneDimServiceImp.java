package com.seaboxdata.cqny.record.service.imp;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.dao.IReportDefinedUnitOneDimDao;
import com.seaboxdata.cqny.record.entity.onedim.ColumDefined;
import com.seaboxdata.cqny.record.entity.onedim.UnitDefined;
import com.seaboxdata.cqny.record.service.ReportDefinedUnitOneDimService;
import com.webapp.support.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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

    @Override
    public List<UnitDefined> getUnitsByOrigin(String originId) {
        List<UnitDefined> unitList = reportDefinedUnitOneDimDao.getUnitByOrigin(originId);
        return unitList;
    }

    @Override
    public List<ColumDefined> getColumByUnit(String unitId) {
        List<ColumDefined> columList = reportDefinedUnitOneDimDao.getColumByUnit(unitId);
        return columList;
    }

    @Override
    public void deleteOneDim(String columId) {
        reportDefinedUnitOneDimDao.deleteOneDim(columId);
    }

    @Override
    public ColumDefined getOnedimColumn(String columId) {
        ColumDefined columDefined = reportDefinedUnitOneDimDao.getOnedimColumn(columId);
        return columDefined;
    }

    @Override
    public void editSaveOnedim(ColumDefined columDefined) {
        reportDefinedUnitOneDimDao.editSaveOnedim(columDefined);
    }


    /**
     * 多条件分页查询
     * @return
     */
    @Override
    public PageResult pagerOnedimListDynamic(Integer currPage, Integer pageSize, Integer unitId, String group_id) {
        Page<Map<String, Object>> pageData = reportDefinedUnitOneDimDao.pagerOnedimListDynamic(currPage, pageSize, unitId, group_id);
        PageResult pageResult = PageResult.pageHelperList2PageResult(pageData);
        return pageResult;
    }

    /**
     * 获取报送单元项组
     * @param unitId
     * @return
     */
    @Override
    public List<Map> getGroupByUnit(String unitId){
        return reportDefinedUnitOneDimDao.getGroupByUnit(unitId);
    }

    /**
     * 批量保存报送单元
     * @param maps
     */
    @Override
    public void editSaveOnedimBat(Map<String, List<ColumDefined>> maps)
    {
        if(maps.containsKey("add")){
            int group_id = getTimestamp();//有BUG，不支持多人同步
            for(ColumDefined mod1 : maps.get("add")){
                Integer gid = mod1.getGroup_id();
                if(gid == null)
                    mod1.setGroup_id(group_id);
                reportDefinedUnitOneDimDao.addSaveOnedim(mod1);
            }
        }
        if(maps.containsKey("edit")){
            for(ColumDefined mod2 : maps.get("edit")){
                reportDefinedUnitOneDimDao.editSaveOnedim(mod2);
            }
        }
        if(maps.containsKey("del")){
            for(ColumDefined mod3 : maps.get("del")){
                String column_id = mod3.getColum_id().toString();
                if(column_id != null && !"".equals(column_id))
                    reportDefinedUnitOneDimDao.deleteOneDim(column_id);
            }
        }
    }
    /**
     * 时间戳转时间(10位时间戳)
     * @return
     */
    public static int getTimestamp() {
        long timeLong = System.currentTimeMillis();
        Date dt = new Date(timeLong);
        Long currentTime = dt.getTime() / 1000L; // 即时毫秒数
        return currentTime.intValue();
    }

    /**
     * 批量删除
     * @param group_id
     */
    public void deleteOneDimDynamic(Integer unitId, String group_id){
        Page<Map<String, Object>> pageData = reportDefinedUnitOneDimDao.pagerOnedimListDynamic(1, 10000, unitId, group_id);
        for(Map<String, Object> map : pageData){
            reportDefinedUnitOneDimDao.deleteOneDim(map.get("colum_id").toString());
        }
    }
}
