package com.seaboxdata.cqny.record.entity;

import com.webapp.support.json.JsonSupport;

public class ReportCustomerData {
    private Integer report_id;
    private String unit_id;
    private String report_group_id;
    private String colum_id;
    private String dimensions_id;
    private String report_data;

    public Integer getReport_id() {
        return report_id;
    }

    public void setReport_id(Integer report_id) {
        this.report_id = report_id;
    }

    public String getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(String unit_id) {
        this.unit_id = unit_id;
    }

    public String getReport_group_id() {
        return report_group_id;
    }

    public void setReport_group_id(String report_group_id) {
        this.report_group_id = report_group_id;
    }

    public String getColum_id() {
        return colum_id;
    }

    public void setColum_id(String colum_id) {
        this.colum_id = colum_id;
    }

    public String getDimensions_id() {
        return dimensions_id;
    }

    public void setDimensions_id(String dimensions_id) {
        this.dimensions_id = dimensions_id;
    }

    public String getReport_data() {
        return report_data;
    }

    public void setReport_data(String report_data) {
        this.report_data = report_data;
    }

    public String toString(){
        return JsonSupport.objectToJson(this);
    }
}
