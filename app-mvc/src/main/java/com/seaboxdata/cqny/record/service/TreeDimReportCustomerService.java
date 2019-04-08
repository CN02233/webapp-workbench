package com.seaboxdata.cqny.record.service;

import com.seaboxdata.cqny.record.controller.TreeDimReportCustomerController;
import com.seaboxdata.cqny.record.entity.treedim.TreeUnitContext;

import java.util.ArrayList;
import java.util.Map;

public interface TreeDimReportCustomerService {

    void saveTreeData(ArrayList<TreeUnitContext> treeUnitContexts);
}
