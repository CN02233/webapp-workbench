package com.seaboxdata.cqny.record.service.imp;

import com.seaboxdata.cqny.record.dao.IReportCustomerDao;
import com.seaboxdata.cqny.record.entity.FomularTmpEntity;
import com.seaboxdata.cqny.record.entity.ReportCustomerData;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;
import com.seaboxdata.cqny.record.entity.treedim.TreeUnitContext;
import com.seaboxdata.cqny.record.service.FomularService;
import com.seaboxdata.cqny.record.service.ReportCustomerService;
import com.seaboxdata.cqny.record.service.TreeDimReportCustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("treeDimReportCustomerService")
public class TreeDimReportCustomerServiceImp implements TreeDimReportCustomerService {

    Logger logger = LoggerFactory.getLogger(TreeDimReportCustomerServiceImp.class);

    @Autowired
    private ReportCustomerService reportCustomerService;

    @Autowired
    private IReportCustomerDao reportCustomerDao;

    @Autowired
    private FomularService fomularService;

    @Override
    @Transactional(rollbackFor = Exception.class,propagation= Propagation.REQUIRED)
    public void saveTreeData(ArrayList<TreeUnitContext> treeUnitContexts) {
        int forTime = 0;

        List<FomularTmpEntity> allFomulars = new ArrayList<>();
        List<ReportCustomerData> checkRefreshFomular = new ArrayList<>();
        for (TreeUnitContext treeUnitContext : treeUnitContexts) {
            String groupId = treeUnitContext.getGroupId();
            ArrayList<ReportCustomerData> columDatas = treeUnitContext.getColumDatas();
            ArrayList<SimpleColumDefined> definedColums = treeUnitContext.getDefinedColums();

            if(forTime==0){
                reportCustomerDao.removeUnitContextData(columDatas.get(0).getUnit_id());
            }

            Map<String, Object> custOrFomular = reportCustomerService.checkCustOrFomular(definedColums, columDatas);

            List<ReportCustomerData> custDataArray = (List<ReportCustomerData>) custOrFomular.get("custDataArray");
            List<FomularTmpEntity> fomularArray = (List<FomularTmpEntity>) custOrFomular.get("fomularArray");
            allFomulars.addAll(fomularArray);

            for (ReportCustomerData columData : custDataArray) {
                reportCustomerDao.insertUnitContext(columData);
            }

            checkRefreshFomular.addAll(custDataArray);

            forTime++;
        }

        if(allFomulars!=null&&allFomulars.size()>0){
            for (FomularTmpEntity fomularTmpEntity : allFomulars) {
                Object fomularDataResult = reportCustomerService.getSimpleFomularData(fomularTmpEntity);
                ReportCustomerData comularData = new ReportCustomerData();
                comularData.setReport_id(fomularTmpEntity.getReportId());
                comularData.setUnit_id(fomularTmpEntity.getUnitId());
                comularData.setColum_id(fomularTmpEntity.getColumId());
                comularData.setDimensions_id(fomularTmpEntity.getDimensionsId());
                comularData.setReport_data(String.valueOf(fomularDataResult));
                comularData.setReport_group_id(fomularTmpEntity.getReportGroupId());
                checkRefreshFomular.add(comularData);

                reportCustomerDao.insertUnitContext(comularData);
            }
        }

        new Thread(new Runnable(){
            public void run(){
                logger.info("刷新关联到用户输入项的公式值");
                List<ReportCustomerData> needRefresDatas = fomularService.refreshFomularForCustInput(checkRefreshFomular);
                logger.info("需要刷新的内容{}",needRefresDatas);
            }
        }).start();




    }
}
