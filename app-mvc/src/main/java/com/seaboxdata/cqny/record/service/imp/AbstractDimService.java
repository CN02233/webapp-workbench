package com.seaboxdata.cqny.record.service.imp;

import com.seaboxdata.cqny.record.config.ColumType;
import com.seaboxdata.cqny.record.dao.IReportDefinedUnitOneDimDao;
import com.seaboxdata.cqny.record.entity.CopyReportDefinedTmp;
import com.seaboxdata.cqny.record.entity.UnitDefined;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AbstractDimService {

    @Autowired
    private IReportDefinedUnitOneDimDao reportDefinedUnitOneDimDao;

    public List<CopyReportDefinedTmp> copySimpleDim(Map<Integer, UnitDefined> fromAndToUnits,Integer targetUnitType) {
        List<CopyReportDefinedTmp> copyReportDefinedTmps = new ArrayList<>();
        if(fromAndToUnits!=null){
            for (Integer fromUnitId : fromAndToUnits.keySet()) {
                UnitDefined toUnit = fromAndToUnits.get(fromUnitId);

                if(!toUnit.getUnit_type().equals(targetUnitType)){
                    continue;
                }

                Integer toUnitId = toUnit.getUnit_id();
                CopyReportDefinedTmp copyReportDefinedTmp = new CopyReportDefinedTmp();
                copyReportDefinedTmp.setFromUnit(fromUnitId);
                copyReportDefinedTmp.setToUnit(toUnitId);

                List<SimpleColumDefined> colums = reportDefinedUnitOneDimDao.getColumByUnit(fromUnitId.toString());
                if(colums!=null){
                    for (SimpleColumDefined colum : colums) {
                        Integer fromColumId = colum.getColum_id().intValue();
                        colum.setColum_id(null);
                        colum.setUnit_id(toUnitId);
                        reportDefinedUnitOneDimDao.addSaveOnedim(colum);

                        copyReportDefinedTmp.getFromAndToColumId().put(fromColumId,colum.getColum_id());


                        if(ColumType.FORMULA.compareWith(new Integer(colum.getColum_type()))){
                            copyReportDefinedTmp.getIsFomular().add(Boolean.TRUE);
                        }
                        else{
                            copyReportDefinedTmp.getIsFomular().add(Boolean.FALSE);
                        }
                    }
                }

                copyReportDefinedTmps.add(copyReportDefinedTmp);
            }
        }

        return copyReportDefinedTmps;
    }

}
