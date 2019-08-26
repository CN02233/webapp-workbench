package com.seaboxdata.cqny.record.entity;

import com.webapp.support.json.JsonSupport;

import java.util.List;

public class SubmitReportRequestEntity {
    private String defined_id;
    private String report_start_date;
    private String report_end_date;
    private List<String> submit_origins;
    private List<String> approve_check_origins;
    private List<String> review_check_origins;

    private String report_data_start;
    private String report_data_end;

    public String getDefined_id() {
        return defined_id;
    }

    public void setDefined_id(String defined_id) {
        this.defined_id = defined_id;
    }

    public String getReport_start_date() {
        return report_start_date;
    }

    public void setReport_start_date(String report_start_date) {
        this.report_start_date = report_start_date;
    }

    public String getReport_end_date() {
        return report_end_date;
    }

    public void setReport_end_date(String report_end_date) {
        this.report_end_date = report_end_date;
    }

    public List<String> getSubmit_origins() {
        return submit_origins;
    }

    public void setSubmit_origins(List<String> submit_origins) {
        this.submit_origins = submit_origins;
    }

    public String getReport_data_start() {
        return report_data_start;
    }

    public void setReport_data_start(String report_data_start) {
        this.report_data_start = report_data_start;
    }

    public String getReport_data_end() {
        return report_data_end;
    }

    public void setReport_data_end(String report_data_end) {
        this.report_data_end = report_data_end;
    }

    public List<String> getApprove_check_origins() {
        return approve_check_origins;
    }

    public void setApprove_check_origins(List<String> approve_check_origins) {
        this.approve_check_origins = approve_check_origins;
    }

    public List<String> getReview_check_origins() {
        return review_check_origins;
    }

    public void setReview_check_origins(List<String> review_check_origins) {
        this.review_check_origins = review_check_origins;
    }

    public String toString(){
        return JsonSupport.objectToJson(this);
    }
}
