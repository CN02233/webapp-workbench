package com.seaboxdata.cqny.record.entity;

import java.util.List;

public class ExcelReportSheet extends ExcelTemplateSheet {

    private List<ExcelReportCell> excelReportCells;


    public List<ExcelReportCell> getExcelReportCells() {
        return excelReportCells;
    }

    public void setExcelReportCells(List<ExcelReportCell> excelReportCells) {
        this.excelReportCells = excelReportCells;
    }
}
