package com.seaboxdata.cqny.record.entity;

import java.util.Date;
import java.util.List;

public class UnitDefined {
    private Integer unit_id;
    private String unit_name;
    private Integer report_defined_id;
    private Integer unit_type;
    private Date create_date;
    private Integer create_user;
    private List colums;

    //merge from UnitEntity.java,remove UnityEntity after merged
    private Integer origin_id;
    private String status;
    private String create_time;
    //辅助属性、前端显示name
    private String origin_name;
    private String user_name;
    private Integer unit_order;//填报顺序

    //对哪类用户展示该单元 0:填报人员 1:审核人员 2：监管人员 3：审核+监管人员
    private String unit_show_type;


    public Integer getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(Integer unit_id) {
        this.unit_id = unit_id;
    }

    public String getUnit_name() {
        return unit_name;
    }

    public void setUnit_name(String unit_name) {
        this.unit_name = unit_name;
    }

    public Integer getReport_defined_id() {
        return report_defined_id;
    }

    public void setReport_defined_id(Integer report_defined_id) {
        this.report_defined_id = report_defined_id;
    }

    public Integer getUnit_type() {
        return unit_type;
    }

    public void setUnit_type(Integer unit_type) {
        this.unit_type = unit_type;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public Integer getCreate_user() {
        return create_user;
    }

    public void setCreate_user(Integer create_user) {
        this.create_user = create_user;
    }

    public List getColums() {
        return colums;
    }

    public void setColums(List colums) {
        this.colums = colums;
    }

    public Integer getOrigin_id() {
        return origin_id;
    }

    public void setOrigin_id(Integer origin_id) {
        this.origin_id = origin_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getOrigin_name() {
        return origin_name;
    }

    public void setOrigin_name(String origin_name) {
        this.origin_name = origin_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public Integer getUnit_order() {
        return unit_order;
    }

    public void setUnit_order(Integer unit_order) {
        this.unit_order = unit_order;
    }

    public String getUnit_show_type() {
        return unit_show_type;
    }

    public void setUnit_show_type(String unit_show_type) {
        this.unit_show_type = unit_show_type;
    }
}
