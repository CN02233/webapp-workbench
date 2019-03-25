package com.seaboxdata.cqny.record.entity;

import java.util.List;

public class ReportUnitCustomerContext {
    private List definedColums;
    private List columDatas;

    public List getDefinedColums() {
        return definedColums;
    }

    public void setDefinedColums(List definedColums) {
        this.definedColums = definedColums;
    }

    public List getColumDatas() {
        return columDatas;
    }

    public void setColumDatas(List columDatas) {
        this.columDatas = columDatas;
    }
}
