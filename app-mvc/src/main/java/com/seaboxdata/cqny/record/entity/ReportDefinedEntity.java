package com.seaboxdata.cqny.record.entity;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.seaboxdata.cqny.record.entity.onedim.UnitDefined;

import java.util.Date;

/**
 * 表report_defined
 */
public class ReportDefinedEntity {

    private Integer defined_id;
    private String defined_name;
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date create_date;
    private Integer origin_id;
    private String status;
    private int create_user;
    //辅助属性、前端显示name
    private String origin_name;
    private String user_name;

    //add by SongChaoqun
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

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
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

    public int getCreate_user() {
        return create_user;
    }

    public void setCreate_user(int create_user) {
        this.create_user = create_user;
    }

    public List<UnitDefined> getUnits() {
        return units;
    }

    public void setUnits(List<UnitDefined> units) {
        this.units = units;
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
}
