package com.seaboxdata.cqny.record.entity;


import com.webapp.support.json.JsonSupport;

import java.util.Date;

public class ReportInfo {

    private Integer reportId;

    private String reportName;

    private String reportPath;

    private String reportCreate;

    private String reportCreateDate;

    private String reportTemplateName;

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportPath() {
        return reportPath;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    public String getReportCreate() {
        return reportCreate;
    }

    public void setReportCreate(String reportCreate) {
        this.reportCreate = reportCreate;
    }

    public String getReportCreateDate() {
        return reportCreateDate;
    }

    public void setReportCreateDate(String reportCreateDate) {
        this.reportCreateDate = reportCreateDate;
    }

    public String getReportTemplateName() {
        return reportTemplateName;
    }

    public void setReportTemplateName(String reportTemplateName) {
        this.reportTemplateName = reportTemplateName;
    }

    public String toString(){
        return JsonSupport.objectToJson(this);
    }
}
