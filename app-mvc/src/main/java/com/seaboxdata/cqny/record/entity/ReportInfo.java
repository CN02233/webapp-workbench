package com.seaboxdata.cqny.record.entity;


import com.webapp.support.json.JsonSupport;

import java.util.Date;
import java.util.List;

public class ReportInfo {

    private Integer reportId;

    private String reportName;

    private String reportPath;

    private String reportCreate;

    private String reportCreateName;

    private String reportCreateDate;

    private String reportTemplateId;

    private String reportStatus = "0";

    private Integer lockedUser;

    private String lockUserName;

    private Integer reviewUser;

    private Integer confirmUser;

    private String reviewUserName;

    private String confirmUserName;

    private List<ExcelTemplateSheet> excelReportSheets;

    public ReportInfo() {
    }

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

    public String toString(){
        return JsonSupport.objectToJson(this);
    }

    public String getReportTemplateId() {
        return reportTemplateId;
    }

    public void setReportTemplateId(String reportTemplateId) {
        this.reportTemplateId = reportTemplateId;
    }

    public List<ExcelTemplateSheet> getExcelReportSheets() {
        return excelReportSheets;
    }

    public void setExcelReportSheets(List<ExcelTemplateSheet> excelReportSheets) {
        this.excelReportSheets = excelReportSheets;
    }

    public String getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(String reportStatus) {
        this.reportStatus = reportStatus;
    }

    public Integer getLockedUser() {
        return lockedUser;
    }

    public void setLockedUser(Integer lockedUser) {
        this.lockedUser = lockedUser;
    }

    public String getReportCreateName() {
        return reportCreateName;
    }

    public void setReportCreateName(String reportCreateName) {
        this.reportCreateName = reportCreateName;
    }

    public String getLockUserName() {
        return lockUserName;
    }

    public void setLockUserName(String lockUserName) {
        this.lockUserName = lockUserName;
    }

    public Integer getReviewUser() {
        return reviewUser;
    }

    public void setReviewUser(Integer reviewUser) {
        this.reviewUser = reviewUser;
    }

    public Integer getConfirmUser() {
        return confirmUser;
    }

    public void setConfirmUser(Integer confirmUser) {
        this.confirmUser = confirmUser;
    }

    public String getReviewUserName() {
        return reviewUserName;
    }

    public void setReviewUserName(String reviewUserName) {
        this.reviewUserName = reviewUserName;
    }

    public String getConfirmUserName() {
        return confirmUserName;
    }

    public void setConfirmUserName(String confirmUserName) {
        this.confirmUserName = confirmUserName;
    }
}
