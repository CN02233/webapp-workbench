package com.seaboxdata.cqny.record.entity;

public class FomularTmpEntity{
    private Integer reportId;
    private String unitId;
    private String columId;
    private String dimensionsId;
    private String reportGroupId;
    private String fomularScript;
    private String old_report_data;

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getColumId() {
        return columId;
    }

    public void setColumId(String columId) {
        this.columId = columId;
    }

    public String getDimensionsId() {
        return dimensionsId;
    }

    public void setDimensionsId(String dimensionsId) {
        this.dimensionsId = dimensionsId;
    }

    public String getReportGroupId() {
        return reportGroupId;
    }

    public void setReportGroupId(String reportGroupId) {
        this.reportGroupId = reportGroupId;
    }

    public String getFomularScript() {
        return fomularScript;
    }

    public void setFomularScript(String fomularScript) {
        this.fomularScript = fomularScript;
    }

    public String getOld_report_data() {
        return old_report_data;
    }

    public void setOld_report_data(String old_report_data) {
        this.old_report_data = old_report_data;
    }
}