package com.seaboxdata.cqny.record.service.imp;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.dao.IReportDefinedUnitOneDimDao;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.entity.onedim.UnitDefined;
import com.seaboxdata.cqny.record.service.ReportDefinedUnitOneDimService;
import com.webapp.support.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
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
    public void addSaveOnedim(SimpleColumDefined simpleColumDefined) {
        reportDefinedUnitOneDimDao.addSaveOnedim(simpleColumDefined);
    }

    @Override
    public List<UnitDefined> getUnitsByOrigin(String originId) {
        List<UnitDefined> unitList = reportDefinedUnitOneDimDao.getUnitByOrigin(originId);
        return unitList;
    }

    @Override
    public List<SimpleColumDefined> getColumByUnit(String unitId) {
        List<SimpleColumDefined> columList = reportDefinedUnitOneDimDao.getColumByUnit(unitId);
        return columList;
    }

    @Override
    public void deleteOneDim(String columId) {
        reportDefinedUnitOneDimDao.deleteOneDim(columId);
    }

    @Override
    public SimpleColumDefined getOnedimColumn(String columId) {
        SimpleColumDefined simpleColumDefined = reportDefinedUnitOneDimDao.getOnedimColumn(columId);
        return simpleColumDefined;
    }

    @Override
    public void editSaveOnedim(SimpleColumDefined simpleColumDefined) {
        reportDefinedUnitOneDimDao.editSaveOnedim(simpleColumDefined);
    }


    /**
     * 多条件分页查询
     * @return
     */
    @Override
    public PageResult pagerOnedimListDynamic(Integer currPage, Integer pageSize, Integer unitId, Map<String,Object> map) {
        Page<Map<String, Object>> pageData = reportDefinedUnitOneDimDao.pagerOnedimListDynamic(currPage, pageSize, unitId, map);
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
     * 保存一维动态报送单元
     * @param maps
     */
    @Override
    public void editSaveOnedimDynamic(SimpleColumDefined group, Map<String, List<SimpleColumDefined>> maps) {
        //是否存在组，不存在新增一行
        Integer group_id = group.getColum_id();
        String group_name = group.getColum_name_cn();
        Integer save_group_id = null;
        if(group_id == null || group_id.equals(0)){
            group.setColum_type("2");
            group.setColum_type("1");
            reportDefinedUnitOneDimDao.addSaveOnedim(group);
            save_group_id = group.getColum_id();
        }else{
            reportDefinedUnitOneDimDao.editSaveOnedim(group);
            save_group_id = Integer.valueOf(group_id);
        }
        if(maps.containsKey("add")){
            for(SimpleColumDefined mod1 : maps.get("add")){
                mod1.setGroup_id(save_group_id);
                mod1.setGroup_name(group_name);
                reportDefinedUnitOneDimDao.addSaveOnedim(mod1);
            }
        }
        if(maps.containsKey("edit")){
            for(SimpleColumDefined mod2 : maps.get("edit")){
                mod2.setGroup_name(group_name);
                reportDefinedUnitOneDimDao.editSaveOnedim(mod2);
            }
        }
        if(maps.containsKey("del")){
            for(SimpleColumDefined mod3 : maps.get("del")){
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
        Map<String,Object> m = new HashMap<>();
        String gid = (group_id == null || "".equals(group_id)) ? "0" : group_id;
        m.put("inc_group_id",gid);
        Page<Map<String, Object>> pageData = reportDefinedUnitOneDimDao.pagerOnedimListDynamic(1, 10000, unitId, m);
        for(Map<String, Object> map : pageData){
            reportDefinedUnitOneDimDao.deleteOneDim(map.get("colum_id").toString());
        }
    }

    @Override
    public PageResult pagerMultdimListStatic(Integer currPage, Integer pageSize, Integer unitId, Map<String,Object> map){
        Page<Map<String, Object>> pageData = reportDefinedUnitOneDimDao.pagerMultdimListStatic(currPage, pageSize, unitId, map);
        PageResult pageResult = PageResult.pageHelperList2PageResult(pageData);
        return pageResult;
    }
}
