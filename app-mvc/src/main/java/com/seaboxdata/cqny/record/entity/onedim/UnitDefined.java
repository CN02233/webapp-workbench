package com.seaboxdata.cqny.record.entity.onedim;

import java.util.Date;
import java.util.List;

public class UnitDefined {
    private Integer unit_id;
    private String unit_name;
    private Integer report_defined_id;
    private Integer unit_type;
    private Date create_date;
    private Integer create_user;
    private List<ColumDefined> colums;

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

    public List<ColumDefined> getColums() {
        return colums;
    }

    public void setColums(List<ColumDefined> colums) {
        this.colums = colums;
    }
}
