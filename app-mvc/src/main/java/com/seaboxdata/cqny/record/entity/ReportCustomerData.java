package com.seaboxdata.cqny.record.entity;

import com.google.common.base.Strings;
import com.webapp.support.json.JsonSupport;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportCustomerData {
    private Integer report_id;
    private String unit_id;
    private String report_group_id;
    private String colum_id;
    private String dimensions_id;
    private String report_data;

    private Date report_start_date;
    private String report_start_date_str;
    private Date report_end_date;
    private String report_end_date_str;

    private Integer colum_order;

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

    public Date getReport_start_date() {
        return report_start_date;
    }

    public void setReport_start_date(Date report_start_date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        this.report_start_date = report_start_date;
        if(report_start_date!=null)
            this.report_start_date_str = format.format(this.report_start_date);
    }

    public String getReport_start_date_str() {
        return report_start_date_str;
    }

    public void setReport_start_date_str(String report_start_date_str) {
        this.report_start_date_str = report_start_date_str;
    }

    public Date getReport_end_date() {
        return report_end_date;
    }

    public void setReport_end_date(Date report_end_date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        this.report_end_date = report_end_date;
        if(report_end_date!=null)
            this.report_end_date_str = format.format(this.report_end_date);
    }

    public String getReport_end_date_str() {
        return report_end_date_str;
    }

    public void setReport_end_date_str(String report_end_date_str) {
        this.report_end_date_str = report_end_date_str;
    }

    public Integer getColum_order() {
        return colum_order;
    }

    public void setColum_order(Integer colum_order) {
        this.colum_order = colum_order;
    }
}
