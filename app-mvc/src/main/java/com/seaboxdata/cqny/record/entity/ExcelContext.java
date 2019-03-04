package com.seaboxdata.cqny.record.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExcelContext {

    private List<List<String>> reportRows = new ArrayList<>();

    private List<ExcelTemplateCellMerged> mergedList = new ArrayList<>();

    public List<ExcelTemplateCellMerged> getMergedList() {
        return mergedList;
    }

    public void setMergedList(List<ExcelTemplateCellMerged> mergedList) {
        this.mergedList = mergedList;
    }

    public List<List<String>> getReportRows() {
        return reportRows;
    }

    public void setReportRows(List<List<String>> reportRows) {
        this.reportRows = reportRows;
    }
}
