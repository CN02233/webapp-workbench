package com.seaboxdata.cqny.reportunit.entity;

import java.util.List;

public class UnitEntity {

    private Integer unit_id;
    private String unit_name;
    private Integer origin_id;
    private String status;
    private String create_time;
    private int create_user;
    //辅助属性、前端显示name
    private String origin_name;
    private String user_name;

    //add by SongChaoqun
    private Integer report_defined_id;//属于哪个报表定义
    private Integer unit_type;//报送类型：单元类型一维静态 一维动态 多维树状 多维静态 详见`UnitDefinedType
    private Integer unit_order;//填报顺序
    private List colums;


    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getOrigin_name() {
        return origin_name;
    }

    public void setOrigin_name(String origin_name) {
        this.origin_name = origin_name;
    }

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

    public int getCreate_user() {
        return create_user;
    }

    public void setCreate_user(int create_user) {
        this.create_user = create_user;
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

    public List getColums() {
        return colums;
    }

    public void setColums(List colums) {
        this.colums = colums;
    }

    public Integer getUnit_order() {
        return unit_order;
    }

    public void setUnit_order(Integer unit_order) {
        this.unit_order = unit_order;
    }
}
