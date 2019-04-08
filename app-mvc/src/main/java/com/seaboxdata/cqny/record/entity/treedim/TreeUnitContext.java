package com.seaboxdata.cqny.record.entity.treedim;

import com.seaboxdata.cqny.record.entity.ReportCustomerData;
import com.seaboxdata.cqny.record.entity.onedim.SimpleColumDefined;

import java.util.ArrayList;

public class TreeUnitContext{

    private String groupId;
    private ArrayList<ReportCustomerData> columDatas;
    private ArrayList<SimpleColumDefined> definedColums;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public ArrayList<ReportCustomerData> getColumDatas() {
        return columDatas;
    }

    public void setColumDatas(ArrayList<ReportCustomerData> columDatas) {
        this.columDatas = columDatas;
    }

    public ArrayList<SimpleColumDefined> getDefinedColums() {
        return definedColums;
    }

    public void setDefinedColums(ArrayList<SimpleColumDefined> definedColums) {
        this.definedColums = definedColums;
    }
}
