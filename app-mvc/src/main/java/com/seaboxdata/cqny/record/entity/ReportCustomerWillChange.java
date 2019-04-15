package com.seaboxdata.cqny.record.entity;

/**
 * 表report_customer
 */
public class ReportCustomerWillChange {

    private Integer report_id;
    private Integer report_defined_id;
    private String report_name;
    private String report_origin;
    private String create_date;
    private String last_modify_user;
    private String report_start_date;
    private String report_end_date;
    private String active_unit;
    private String report_status;
    private String user_name;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

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

    public String getReport_origin() {
        return report_origin;
    }

    public void setReport_origin(String report_origin) {
        this.report_origin = report_origin;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getLast_modify_user() {
        return last_modify_user;
    }

    public void setLast_modify_user(String last_modify_user) {
        this.last_modify_user = last_modify_user;
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

    public String getActive_unit() {
        return active_unit;
    }

    public void setActive_unit(String active_unit) {
        this.active_unit = active_unit;
    }

    public String getReport_status() {
        return report_status;
    }

    public void setReport_status(String report_status) {
        this.report_status = report_status;
    }
}
