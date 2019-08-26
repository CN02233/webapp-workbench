package com.seaboxdata.cqny.datamove.entity;

public class DataMoveConfig {

    private String defined_id;
    private String old_unit_table_name  ;
    private String new_unit_id;
    private String old_unit_row_name;
    private String new_unit_colum_id;
    private String old_unit_colum_name;
    private String new_unit_dim_id;
    private Integer rmb_unit;

    public String getDefined_id() {
        return defined_id;
    }

    public void setDefined_id(String defined_id) {
        this.defined_id = defined_id;
    }

    public String getOld_unit_table_name() {
        return old_unit_table_name;
    }

    public void setOld_unit_table_name(String old_unit_table_name) {
        this.old_unit_table_name = old_unit_table_name;
    }

    public String getNew_unit_id() {
        return new_unit_id;
    }

    public void setNew_unit_id(String new_unit_id) {
        this.new_unit_id = new_unit_id;
    }

    public String getOld_unit_row_name() {
        return old_unit_row_name;
    }

    public void setOld_unit_row_name(String old_unit_row_name) {
        this.old_unit_row_name = old_unit_row_name;
    }

    public String getNew_unit_colum_id() {
        return new_unit_colum_id;
    }

    public void setNew_unit_colum_id(String new_unit_colum_id) {
        this.new_unit_colum_id = new_unit_colum_id;
    }

    public String getOld_unit_colum_name() {
        return old_unit_colum_name;
    }

    public void setOld_unit_colum_name(String old_unit_colum_name) {
        this.old_unit_colum_name = old_unit_colum_name;
    }

    public String getNew_unit_dim_id() {
        return new_unit_dim_id;
    }

    public void setNew_unit_dim_id(String new_unit_dim_id) {
        this.new_unit_dim_id = new_unit_dim_id;
    }

    public Integer getRmb_unit() {
        return rmb_unit;
    }

    public void setRmb_unit(Integer rmb_unit) {
        this.rmb_unit = rmb_unit;
    }
}
