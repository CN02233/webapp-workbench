package com.seaboxdata.cqny.record.entity;

import com.webapp.support.json.JsonSupport;

public class RememberCustData {
    private Integer id;
    private Integer user_id;
    private Integer report_id;
    private Integer unit_id;
    private Integer colum_id;
    private Integer dimensions_id;
    private String remember_data;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(Integer unit_id) {
        this.unit_id = unit_id;
    }

    public Integer getColum_id() {
        return colum_id;
    }

    public void setColum_id(Integer colum_id) {
        this.colum_id = colum_id;
    }

    public Integer getDimensions_id() {
        return dimensions_id;
    }

    public void setDimensions_id(Integer dimensions_id) {
        this.dimensions_id = dimensions_id;
    }

    public String getRemember_data() {
        return remember_data;
    }

    public void setRemember_data(String remember_data) {
        this.remember_data = remember_data;
    }

    public Integer getReport_id() {
        return report_id;
    }

    public void setReport_id(Integer report_id) {
        this.report_id = report_id;
    }

    public String toString(){
        return JsonSupport.objectToJson(this);
    }
}
