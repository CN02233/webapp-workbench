package com.seaboxdata.cqny.record.service;

import com.seaboxdata.cqny.record.entity.RememberCustData;
import com.seaboxdata.cqny.record.entity.ReportCustomerData;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;

import java.util.ArrayList;
import java.util.List;

public interface RememberCustDataService {

    void rememberCustData(ArrayList<SimpleColumDefined> simpleColumDefineds,
                          ArrayList<ReportCustomerData> columDatas,
                          Integer rememberUser);

    boolean needOrNotRemember(SimpleColumDefined simpleColumDefined);

    void doRemember(List<RememberCustData> rememberCustDatas);

    void rememberCustDataByGrid(ArrayList<SimpleColumDefined> simpleColumDefineds,
                          ArrayList<ReportCustomerData> columDatas,
                          Integer rememberUser);
}
