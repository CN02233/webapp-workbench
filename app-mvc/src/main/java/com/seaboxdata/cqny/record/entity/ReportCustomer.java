package com.seaboxdata.cqny.record.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReportCustomer {

    private Integer report_id;
    private Integer report_defined_id;
    private String report_name;
    private Integer report_origin;
    private String report_origin_name;

    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date create_date;
    private Integer last_modify_user;
    private Date report_start_date;
    private String report_start_date_str;

    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date report_end_date;
    private String report_end_date_str;
    private Integer active_unit;
    private List<UnitDefined> unitEntities;
    private String pass_auth;
    private String report_status;
    private Date report_data_start;
    private Date report_data_end;

    //merge from record.reportunit.ReportCustomer
    private String user_name;


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
        if(report_start_date!=null){
            this.report_start_date_str = (new SimpleDateFormat("yyyy-MM-dd").format(report_start_date));
        }
    }

    public Date getReport_end_date() {
        return report_end_date;
    }

    public void setReport_end_date(Date report_end_date) {
        this.report_end_date = report_end_date;
        if(report_end_date!=null){
            this.report_end_date_str = (new SimpleDateFormat("yyyy-MM-dd").format(report_end_date));
        }
    }

    public Integer getActive_unit() {
        return active_unit;
    }

    public void setActive_unit(Integer active_unit) {
        this.active_unit = active_unit;
    }

    public List<UnitDefined> getUnitEntities() {
        return unitEntities;
    }

    public void setUnitEntities(List<UnitDefined> unitEntities) {
        this.unitEntities = unitEntities;
    }

    public String getPass_auth() {
        return pass_auth;
    }

    public void setPass_auth(String pass_auth) {
        this.pass_auth = pass_auth;
    }

    public String getReport_status() {
        return report_status;
    }

    public void setReport_status(String report_status) {
        this.report_status = report_status;
    }

    public String getReport_start_date_str() {
        return report_start_date_str;
    }

    public void setReport_start_date_str(String report_start_date_str) {
        this.report_start_date_str = report_start_date_str;
    }

    public String getReport_end_date_str() {
        return report_end_date_str;
    }

    public void setReport_end_date_str(String report_end_date_str) {
        this.report_end_date_str = report_end_date_str;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getReport_origin_name() {
        return report_origin_name;
    }

    public void setReport_origin_name(String report_origin_name) {
        this.report_origin_name = report_origin_name;
    }

    public Date getReport_data_start() {
        return report_data_start;
    }

    public void setReport_data_start(Date report_data_start) {
        this.report_data_start = report_data_start;
    }

    public Date getReport_data_end() {
        return report_data_end;
    }

    public void setReport_data_end(Date report_data_end) {
        this.report_data_end = report_data_end;
    }
}
