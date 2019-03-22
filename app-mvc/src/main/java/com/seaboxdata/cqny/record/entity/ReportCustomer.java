package com.seaboxdata.cqny.record.entity;

import java.util.Date;

public class ReportCustomer {

    private Integer report_id;
    private Integer report_defined_id;
    private String report_name;
    private Integer report_origin;
    private Date create_date;
    private Integer last_modify_user;
    private Date report_start_date;
    private Date report_end_date;

    public Integer getReport_id() {
        return report_id;
    }

    public void setReport_id(Integer report_id) {
        this.report_id = report_id;
    }

    public Integer getReport_defined_id() {
        return report_defined_id;
    }

    public void setReport_defined_id(Integer report_defined_id) {
        this.report_defined_id = report_defined_id;
    }

    public String getReport_name() {
        return report_name;
    }

    public void setReport_name(String report_name) {
        this.report_name = report_name;
    }

    public Integer getReport_origin() {
        return report_origin;
    }

    public void setReport_origin(Integer report_origin) {
        this.report_origin = report_origin;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public Integer getLast_modify_user() {
        return last_modify_user;
    }

    public void setLast_modify_user(Integer last_modify_user) {
        this.last_modify_user = last_modify_user;
    }

    public Date getReport_start_date() {
        return report_start_date;
    }

    public void setReport_start_date(Date report_start_date) {
        this.report_start_date = report_start_date;
    }

    public Date getReport_end_date() {
        return report_end_date;
    }

    public void setReport_end_date(Date report_end_date) {
        this.report_end_date = report_end_date;
    }
}
