package com.seaboxdata.cqny.record.service.imp;

import com.google.common.base.Strings;
import com.seaboxdata.cqny.record.config.UnitDefinedType;
import com.seaboxdata.cqny.record.dao.IRememberCustDataDao;
import com.seaboxdata.cqny.record.entity.RememberCustData;
import com.seaboxdata.cqny.record.entity.ReportCustomerData;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.service.RememberCustDataService;
import com.seaboxdata.cqny.reportunit.dao.IReportUnitDao;
import com.seaboxdata.cqny.reportunit.entity.UnitEntity;
import com.webapp.support.session.SessionSupport;
import com.workbench.auth.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;

@Service("rememberCustDataService")

public class RememberCustDataServiceImp implements RememberCustDataService {

    private static final String NEED_REMEMBER = "Y";
    private static final String NOT_NEED_REMEMBER = "N";

    @Autowired
    private IReportUnitDao reportUnitDao;

    @Autowired
    private IRememberCustDataDao rememberCustDataDao;

    private ThreadLocal<UnitEntity> unitEntity = new ThreadLocal<>();

    private ThreadLocal<Integer> userId = new ThreadLocal<>();

    @Override
    public void rememberCustData(
            ArrayList<SimpleColumDefined> simpleColumDefineds,
            ArrayList<ReportCustomerData> columDatas,Integer rememberUser) {
        userId.set(rememberUser);

        if(simpleColumDefineds!=null&&simpleColumDefineds.size()>0){
            for (SimpleColumDefined simpleColumDefined : simpleColumDefineds) {
                if(this.needOrNotRemember(simpleColumDefined)){
                    if(unitEntity.get()==null){
                        unitEntity.set(reportUnitDao.getReportUnit(simpleColumDefined.getUnit_id().toString()));
                        break;
                    }
                }
            }
        }

        if(columDatas!=null&&columDatas.size()>0){
            for (ReportCustomerData columData : columDatas) {
                RememberCustData rememberData = null;
                rememberData = makeRememberData(columData);
                this.doRemember(rememberData);
            }
        }
    }

    @Override
    public boolean needOrNotRemember(SimpleColumDefined simpleColumDefined) {
        String needRemember = simpleColumDefined.getNeed_remember();
        if(!Strings.isNullOrEmpty(needRemember)&&NEED_REMEMBER.equals(needRemember)){
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doRemember(RememberCustData rememberCustData) {
        rememberCustData.setUser_id(userId.get());
        rememberCustDataDao.deleteRememberCustDataByUnit(
                rememberCustData.getReport_id().toString(),
                rememberCustData.getUnit_id().toString(),
                String.valueOf(userId));

        rememberCustDataDao.saveRememberCustData(rememberCustData);
    }

    public RememberCustData makeRememberData(ReportCustomerData reportCustomerData){
        Integer entityType = unitEntity.get().getUnit_type();
        RememberCustData rememberCustData = new RememberCustData();
        rememberCustData.setReport_id(reportCustomerData.getReport_id());
        rememberCustData.setUnit_id(unitEntity.get().getUnit_id());
        rememberCustData.setRemember_data(reportCustomerData.getReport_data());

        if(UnitDefinedType.ONEDIMSTATIC.compareWith(entityType)||
                UnitDefinedType.ONEDIMDYNAMIC.compareWith(entityType)){
            rememberCustData.setColum_id(new Integer(reportCustomerData.getColum_id()));

        }else if(UnitDefinedType.MANYDIMSTATIC.compareWith(entityType)){
            rememberCustData.setColum_id(new Integer(reportCustomerData.getColum_id()));
            rememberCustData.setDimensions_id(new Integer(reportCustomerData.getDimensions_id()));

        }else if(UnitDefinedType.MANYDIMTREE.compareWith(entityType)){
            rememberCustData.setDimensions_id(new Integer(reportCustomerData.getDimensions_id()));
        }
        return rememberCustData;
    }
}
