package com.seaboxdata.cqny.record.service.imp;

import com.google.common.base.Strings;
import com.seaboxdata.cqny.record.config.UnitDefinedType;
import com.seaboxdata.cqny.record.dao.IRememberCustDataDao;
import com.seaboxdata.cqny.record.entity.RememberCustData;
import com.seaboxdata.cqny.record.entity.ReportCustomerData;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.entity.UnitDefined;
import com.seaboxdata.cqny.record.service.RememberCustDataService;
import com.seaboxdata.cqny.record.dao.IReportUnitDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("rememberCustDataService")

public class RememberCustDataServiceImp implements RememberCustDataService {

    private static final String NEED_REMEMBER = "Y";
    private static final String NOT_NEED_REMEMBER = "N";

    private Logger logger = LoggerFactory.getLogger(RememberCustDataServiceImp.class);

    @Autowired
    private IReportUnitDao reportUnitDao;

    @Autowired
    private IRememberCustDataDao rememberCustDataDao;

    private ThreadLocal<UnitDefined> unitEntity = new ThreadLocal<>();

    private ThreadLocal<Integer> userId = new ThreadLocal<>();

    @Override
    public void rememberCustData(
            ArrayList<SimpleColumDefined> simpleColumDefineds,
            ArrayList<ReportCustomerData> columDatas,Integer rememberUser) {
        userId.set(rememberUser);

        Map<String,SimpleColumDefined> needRememberTmp = new HashMap<>();
        if(simpleColumDefineds!=null&&simpleColumDefineds.size()>0){
            for (SimpleColumDefined simpleColumDefined : simpleColumDefineds) {
                if(this.needOrNotRemember(simpleColumDefined)){
                    if(unitEntity.get()==null){
                        unitEntity.set(reportUnitDao.getReportUnit(simpleColumDefined.getUnit_id().toString()));
                    }
                    Integer entityType = unitEntity.get().getUnit_type();
                    if(UnitDefinedType.ONEDIMSTATIC.compareWith(entityType)||
                            UnitDefinedType.ONEDIMDYNAMIC.compareWith(entityType)||UnitDefinedType.MANYDIMTREE.compareWith(entityType)){
                        needRememberTmp.put(simpleColumDefined.getUnit_id()+"-"+simpleColumDefined.getColum_id(),simpleColumDefined);
                    }else if(UnitDefinedType.MANYDIMSTATIC.compareWith(entityType)){
                        needRememberTmp.put(simpleColumDefined.getUnit_id()+"-"+simpleColumDefined.getColum_id()+"-"+simpleColumDefined.getGroup_id(),simpleColumDefined);

                    }
                }
            }
        }

        if(columDatas!=null&&columDatas.size()>0){
             List<RememberCustData> rememberList = new ArrayList<>();
            for (ReportCustomerData columData : columDatas) {
                if(needRememberTmp.containsKey(columData.getUnit_id()+"-"+columData.getColum_id())||
                        needRememberTmp.containsKey(columData.getUnit_id()+"-"+columData.getDimensions_id())){
                    RememberCustData rememberData = null;
                    rememberData = makeRememberData(columData);
                    rememberList.add(rememberData);
                }
            }

            this.doRemember(rememberList);

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
    public void doRemember(List<RememberCustData> rememberCustDatas) {
        logger.info("当前需要记忆的数据{}",rememberCustDatas);
        boolean removeOld = false;
        for (RememberCustData rememberCustData : rememberCustDatas) {
            if(!removeOld){
                removeOld = true;
                rememberCustDataDao.deleteRememberCustDataByUnit(
                        rememberCustData.getReport_id().toString(),
                        rememberCustData.getUnit_id().toString(),
                        String.valueOf(userId.get()));
            }
            rememberCustData.setUser_id(userId.get());
            rememberCustDataDao.saveRememberCustData(rememberCustData);
        }


    }

    public RememberCustData makeRememberData(ReportCustomerData reportCustomerData){
        Integer entityType = unitEntity.get().getUnit_type();
        RememberCustData rememberCustData = new RememberCustData();
        rememberCustData.setReport_id(reportCustomerData.getReport_id());
        rememberCustData.setUnit_id(unitEntity.get().getUnit_id());
        rememberCustData.setRemember_data(reportCustomerData.getReport_data());

        if(UnitDefinedType.ONEDIMSTATIC.compareWith(entityType)){
            rememberCustData.setColum_id(new Integer(reportCustomerData.getColum_id()));

        }else if(UnitDefinedType.MANYDIMSTATIC.compareWith(entityType)||
                UnitDefinedType.ONEDIMDYNAMIC.compareWith(entityType)){
            rememberCustData.setColum_id(new Integer(reportCustomerData.getColum_id()));
            rememberCustData.setDimensions_id(new Integer(reportCustomerData.getDimensions_id()));

        }else if(UnitDefinedType.MANYDIMTREE.compareWith(entityType)){
            rememberCustData.setDimensions_id(new Integer(reportCustomerData.getDimensions_id()));
        }
        return rememberCustData;
    }

    @Override
    public void rememberCustDataByGrid(
            ArrayList<SimpleColumDefined> simpleColumDefineds,
            ArrayList<ReportCustomerData> columDatas,Integer rememberUser) {
        userId.set(rememberUser);

        Map<String,SimpleColumDefined> needRememberTmp = new HashMap<>();
        if(simpleColumDefineds!=null&&simpleColumDefineds.size()>0){
            for (SimpleColumDefined simpleColumDefined : simpleColumDefineds) {
                if(this.needOrNotRemember(simpleColumDefined)){
                    if(unitEntity.get()==null){
                        unitEntity.set(reportUnitDao.getReportUnit(simpleColumDefined.getUnit_id().toString()));
                    }
                    needRememberTmp.put(simpleColumDefined.getUnit_id()+"-"+simpleColumDefined.getColum_id()+"-"+simpleColumDefined.getGroup_id(),simpleColumDefined);
                }
            }
        }

        if(columDatas!=null&&columDatas.size()>0){
            List<RememberCustData> rememberList = new ArrayList<>();
            for (ReportCustomerData columData : columDatas) {
                String key = columData.getUnit_id()+"-"+columData.getColum_id() + "-" + columData.getDimensions_id();
                if(needRememberTmp.containsKey(key)){
                    RememberCustData rememberData = null;
                    rememberData = makeRememberData(columData);
                    rememberList.add(rememberData);
                }
            }

            this.doRemember(rememberList);

        }
    }
}
