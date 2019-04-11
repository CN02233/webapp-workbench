package com.seaboxdata.cqny.record.entity;

import java.util.List;

public class SubmitReportRequestEntity {
    private String defined_id;
    private String report_start_date;
    private String report_end_date;
    private List<String> check_origins;

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

    public List<String> getCheck_origins() {
        return check_origins;
    }

    public void setCheck_origins(List<String> check_origins) {
        this.check_origins = check_origins;
    }
}
