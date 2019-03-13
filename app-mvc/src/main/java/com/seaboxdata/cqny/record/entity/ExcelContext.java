package com.seaboxdata.cqny.record.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelContext {

    private String sheetName;

    private Integer sheetRows;

    private Integer sheetColums;

    private List<List<String>> reportRows = new ArrayList<>();

    private List<ExcelTemplateCellMerged> mergedList = new ArrayList<>();

    private Map<String,String> formulas = new HashMap<>();

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

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public Integer getSheetRows() {
        return sheetRows;
    }

    public void setSheetRows(Integer sheetRows) {
        this.sheetRows = sheetRows;
    }

    public Integer getSheetColums() {
        return sheetColums;
    }

    public void setSheetColums(Integer sheetColums) {
        this.sheetColums = sheetColums;
    }

    public Map<String, String> getFormulas() {
        return formulas;
    }

    public void setFormulas(Map<String, String> formulas) {
        this.formulas = formulas;
    }
}
