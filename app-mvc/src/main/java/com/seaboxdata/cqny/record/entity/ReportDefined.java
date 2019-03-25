package com.seaboxdata.cqny.record.entity;

import com.seaboxdata.cqny.record.entity.onedim.UnitDefined;

import java.util.Date;
import java.util.List;

public class ReportDefined {
    private Integer defined_id;
    private String defined_name;
    private String defined_describe;
    private Date create_date;
    private Date create_user;
    private List<UnitDefined> units;

    public Integer getDefined_id() {
        return defined_id;
    }

    public void setDefined_id(Integer defined_id) {
        this.defined_id = defined_id;
    }

    public String getDefined_name() {
        return defined_name;
    }

    public void setDefined_name(String defined_name) {
        this.defined_name = defined_name;
    }

    public String getDefined_describe() {
        return defined_describe;
    }

    public void setDefined_describe(String defined_describe) {
        this.defined_describe = defined_describe;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public Date getCreate_user() {
        return create_user;
    }

    public void setCreate_user(Date create_user) {
        this.create_user = create_user;
    }

    public List<UnitDefined> getUnits() {
        return units;
    }

    public void setUnits(List<UnitDefined> units) {
        this.units = units;
    }
}
