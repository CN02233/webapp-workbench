package com.seaboxdata.cqny.record.entity;

import java.util.List;

public class ExcelTemplate {

    private List<ExcelTemplateCell> excelTemplateCell;

    private List<ExcelTemplateCellMerged> excelTemplateCellMerged;

    public List<ExcelTemplateCell> getExcelTemplateCell() {
        return excelTemplateCell;
    }

    public void setExcelTemplateCell(List<ExcelTemplateCell> excelTemplateCell) {
        this.excelTemplateCell = excelTemplateCell;
    }

    public List<ExcelTemplateCellMerged> getExcelTemplateCellMerged() {
        return excelTemplateCellMerged;
    }

    public void setExcelTemplateCellMerged(List<ExcelTemplateCellMerged> excelTemplateCellMerged) {
        this.excelTemplateCellMerged = excelTemplateCellMerged;
    }
}
