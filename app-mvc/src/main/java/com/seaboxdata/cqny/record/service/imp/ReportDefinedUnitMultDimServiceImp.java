package com.seaboxdata.cqny.record.service.imp;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.config.UnitDefinedType;
import com.seaboxdata.cqny.record.dao.IReportDefinedUnitMultDimDao;
import com.seaboxdata.cqny.record.entity.CopyReportDefinedTmp;
import com.seaboxdata.cqny.record.entity.onedim.GridColumDefined;
import com.seaboxdata.cqny.record.entity.UnitDefined;
import com.seaboxdata.cqny.record.service.ReportDefinedUnitMultDimService;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("reportDefinedUnitMultDimService")
public class ReportDefinedUnitMultDimServiceImp extends AbstractDimService implements ReportDefinedUnitMultDimService {

    @Autowired
    private IReportDefinedUnitMultDimDao reportDefinedUnitMultDimDao;

    @Override
    public PageResult pagerMultdimList(Integer unitId,Integer columId, Integer currPage, Integer pageSize) {
        Page<Map<String, Object>> pageData = reportDefinedUnitMultDimDao.pagerMultdimList(unitId, columId, currPage, pageSize);
        PageResult pageResult = PageResult.pageHelperList2PageResult(pageData);
        return pageResult;
    }

    @Override
    public PageResult pagerDimList(Integer unitId, Integer currPage, Integer pageSize){
        Page<Map<String, Object>> pageData = reportDefinedUnitMultDimDao.pagerDimList(unitId, currPage, pageSize);
        PageResult pageResult = PageResult.pageHelperList2PageResult(pageData);
        return pageResult;
    }

    @Override
    public List<UnitDefined> getUnitsByOrigin(String originId) {
        List<UnitDefined> unitList = reportDefinedUnitMultDimDao.getUnitByOrigin(originId);
        return unitList;
    }

    @Override
    public List<GridColumDefined> getMultColumByUnit(String unitId,String unitType) {
        List<GridColumDefined> columList = null;
        if(unitType.equals("3"))
            columList = reportDefinedUnitMultDimDao.getColumByUnit(unitId);
        else
            columList = reportDefinedUnitMultDimDao.getOneColumByUnit(unitId);
        return columList;
    }

    @Override
    public List<GridColumDefined> getMultDefindByUnit(String unitId){
        List<GridColumDefined> columList = reportDefinedUnitMultDimDao.getMultDefindByUnit(unitId);
        return columList;
    }

    @Override
    public List<GridColumDefined> getColumByUnit(String unitId) {
        List<GridColumDefined> columList = reportDefinedUnitMultDimDao.getColumByUnit(unitId);
        return columList;
    }

    @Override
    public List<GridColumDefined> getColumByUnitOutUninon(String unitId) {
        List<GridColumDefined> resultLIst = new ArrayList<>();
        List<GridColumDefined> columList0 = reportDefinedUnitMultDimDao.getColumByUnit0(unitId);
        List<GridColumDefined> columList1 = reportDefinedUnitMultDimDao.getColumByUnit1(unitId);
        List<GridColumDefined> columList2 = reportDefinedUnitMultDimDao.getColumByUnit2(unitId);
        resultLIst.addAll(columList0);
        resultLIst.addAll(columList1);
        resultLIst.addAll(columList2);
        return resultLIst;
    }


    @Override
    public GridColumDefined getOnedimColumn(String columId) {
        GridColumDefined simpleColumDefined = reportDefinedUnitMultDimDao.getOnedimColumn(columId);
        return simpleColumDefined;
    }

    /**
     * 多条件分页查询
     * @return
     */
    @Override
    public PageResult pagerOnedimListDynamic(Integer currPage, Integer pageSize, Integer unitId, Map<String,Object> map) {
        Page<Map<String, Object>> pageData = reportDefinedUnitMultDimDao.pagerOnedimListDynamic(currPage, pageSize, unitId, map);
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
        return reportDefinedUnitMultDimDao.getGroupByUnit(unitId);
    }

    @Override
    public void addSaveMultdim_dim(GridColumDefined maps) {
        reportDefinedUnitMultDimDao.addSaveMultdim_dim(maps);
    }

    @Override
    public void editSaveMultdim_dim(GridColumDefined maps) {
        reportDefinedUnitMultDimDao.editSaveMultdim_dim(maps);
    }

    @Override
    public JsonResult deleteSaveMultdim_dim(GridColumDefined maps) {
        JsonResult res = new JsonResult();
        /*int cnt = reportDefinedUnitMultDimDao.countDimList(maps.getUnit_id(), maps.getDim_id());
        if(cnt>0){
            res.setResult(JsonResult.RESULT.FAILD);
            res.setResult_msg("该维度已生成输入项，请先删除所有关联输入项");
        }*/
        reportDefinedUnitMultDimDao.deleteMultDim(maps.getUnit_id(), null, maps.getDim_id());
        reportDefinedUnitMultDimDao.deleteMultDim_dim(maps.getDim_id());
        res.setResult(JsonResult.RESULT.SUCCESS);
        res.setResult_msg("删除成功");
        return res;
    }

    @Override
    public void saveMultdim_col(GridColumDefined maps){
        Integer colum_id = maps.getColum_id();
        if(colum_id == null || colum_id == 0){
            reportDefinedUnitMultDimDao.addSaveMultdim_col(maps);
        }else{
            reportDefinedUnitMultDimDao.editSaveMultdim_col(maps);
        }
    }

    /**
     * 保存一维动态报送单元
     * @param maps
     */
    @Override
    public void editSaveMultdim(Map<String, List<GridColumDefined>> maps) {

        boolean d = maps.containsKey("add_data");
        if(maps.containsKey("add_dim")){
            for(GridColumDefined mod1 : maps.get("add_dim")){
                reportDefinedUnitMultDimDao.addSaveMultdim_dim(mod1);
                String no = mod1.getDim_id_no();
                if(!d || no == null || "".equals(no))
                    continue;
                for(GridColumDefined mod11 : maps.get("add_data")){
                    if(no.equals(mod11.getDim_id_no()))
                        mod11.setDim_id(mod1.getDim_id());
                }
            }
        }
        if(maps.containsKey("edit_dim")){
            for(GridColumDefined mod1 : maps.get("edit_dim")){
                reportDefinedUnitMultDimDao.editSaveMultdim_dim(mod1);
            }
        }
        if(maps.containsKey("del_dim")){
            for(GridColumDefined mod1 : maps.get("del_dim")){
                Integer dim_id = mod1.getDim_id();
                if(dim_id != null)
                    reportDefinedUnitMultDimDao.deleteMultDim_dim(dim_id);
            }
        }
        if(maps.containsKey("add_col")){
            for(GridColumDefined mod1 : maps.get("add_col")){
                reportDefinedUnitMultDimDao.addSaveMultdim_col(mod1);
                String no = mod1.getColum_id_no();
                String formulano = ".?" + no + ".";
                if(!d || no == null || "".equals(no))
                    continue;
                for(GridColumDefined mod11 : maps.get("add_data")){
                    if(no.equals(mod11.getColum_id_no()))
                        mod11.setColum_id(mod1.getColum_id());
                    //更新公式
                    String script = mod11.getColum_formula();
                    if(script!=null && script.contains(formulano)){
                        String expr = "." + mod1.getColum_id().toString() + ".";
                        mod11.setColum_formula( script.replace(formulano,expr) );
                    }
                }
                for(GridColumDefined mod22 : maps.get("edit_data")){
                    //更新公式
                    String script = mod22.getColum_formula();
                    if(script!=null && script.contains(formulano)){
                        String expr = "." + mod1.getColum_id().toString() + ".";
                        mod22.setColum_formula( script.replace(formulano,expr) );
                    }
                }
            }
        }
        if(maps.containsKey("edit_col")){
            for(GridColumDefined mod1 : maps.get("edit_col")){
                reportDefinedUnitMultDimDao.editSaveMultdim_col(mod1);
            }
        }
        if(maps.containsKey("del_col")){
            for(GridColumDefined mod1 : maps.get("del_col")){
                Integer colum_id = mod1.getColum_id();
                if(colum_id != null)
                    reportDefinedUnitMultDimDao.deleteMultDim_col(colum_id);
            }
        }
        if(maps.containsKey("add_data")){
            for(GridColumDefined mod1 : maps.get("add_data")){
                reportDefinedUnitMultDimDao.addSaveMultdim(mod1);
            }
        }
        if(maps.containsKey("edit_data")){
            for(GridColumDefined mod1 : maps.get("edit_data")){
                reportDefinedUnitMultDimDao.editSaveMultdim(mod1);
            }
        }
        if(maps.containsKey("del_data")){
            for(GridColumDefined mod1 : maps.get("del_data")){
                Integer column_id = mod1.getColum_id();
                Integer unit_id = mod1.getUnit_id();
                Integer dim_id = mod1.getDim_id();
                if(column_id != null && dim_id != null && unit_id != null)
                    reportDefinedUnitMultDimDao.deleteMultDim(unit_id, column_id, dim_id);
            }
        }
    }

    /**
     * 批量删除
     */
    @Override
    public void deleteMultdim(Integer unitId, Integer columId){
        reportDefinedUnitMultDimDao.deleteMultDim(unitId, columId, null);
        reportDefinedUnitMultDimDao.deleteMultDim_col(columId);
    }

    @Override
    public PageResult pagerMultdimListStatic(Integer currPage, Integer pageSize, Integer unitId, Map<String,Object> map){
        Page<Map<String, Object>> pageData = reportDefinedUnitMultDimDao.pagerMultdimListStatic(currPage, pageSize, unitId, map);
        PageResult pageResult = PageResult.pageHelperList2PageResult(pageData);
        return pageResult;
    }

    @Override
    public List<CopyReportDefinedTmp> copyDims(Map<Integer, UnitDefined> fromAndToUnits) {
        List<CopyReportDefinedTmp> copyReportDefinedTmps = new ArrayList<>();

        if(fromAndToUnits!=null) {
            for (Integer fromUnitId : fromAndToUnits.keySet()) {
                UnitDefined toUnit = fromAndToUnits.get(fromUnitId);
                if(UnitDefinedType.MANYDIMSTATIC.compareWith(toUnit.getUnit_type())){
                    List<GridColumDefined> gridColumDefineds = reportDefinedUnitMultDimDao.getMultDefindByUnit(fromUnitId.toString());
                    if(gridColumDefineds!=null){
                        CopyReportDefinedTmp copyReportDefinedTmp = new CopyReportDefinedTmp();
                        copyReportDefinedTmp.setFromUnit(fromUnitId);
                        copyReportDefinedTmp.setToUnit(toUnit.getUnit_id());

                        for (GridColumDefined gridColumDefined : gridColumDefineds) {
                            Integer fromColumId = gridColumDefined.getColum_id().intValue();
                            Integer fromDimId = gridColumDefined.getDim_id().intValue();
                            gridColumDefined.setColum_id(null);
                            gridColumDefined.setDim_id(null);
                            gridColumDefined.setUnit_id(toUnit.getUnit_id());
                            if(!copyReportDefinedTmp.getFromAndToColumId().containsKey(fromColumId)){
                                this.saveMultdim_col(gridColumDefined);
                                copyReportDefinedTmp.getFromAndToColumId().put(fromColumId,gridColumDefined.getColum_id());
                            }else{
                                gridColumDefined.setColum_id(copyReportDefinedTmp.getFromAndToColumId().get(fromColumId));
                            }
                            if(!copyReportDefinedTmp.getFromAndToDimId().containsKey(fromDimId)){
                                this.addSaveMultdim_dim(gridColumDefined);
                                copyReportDefinedTmp.getFromAndToDimId().put(fromDimId,gridColumDefined.getDim_id());
                            }else{
                                gridColumDefined.setDim_id(copyReportDefinedTmp.getFromAndToDimId().get(fromDimId));
                            }
                            reportDefinedUnitMultDimDao.addSaveMultdim(gridColumDefined);
                        }
                        copyReportDefinedTmps.add(copyReportDefinedTmp);
                    }
                }
            }
        }

        return copyReportDefinedTmps;
    }
}
