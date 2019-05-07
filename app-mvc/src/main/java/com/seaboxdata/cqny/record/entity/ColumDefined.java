package com.seaboxdata.cqny.record.entity;

import com.webapp.support.json.JsonSupport;

public class ColumDefined {
    private Integer unit_id;

    private String colum_name;
    private String colum_name_cn;

    private String colum_type;

    private Integer min_value;
    private Integer max_value    ;

    private String colum_point;
    private String colum_desc;

    private String colum_formula    ;
    private String colum_formula_desc    ;


    public String getColum_type() {
        return colum_type;
    }

    public void setColum_type(String colum_type) {
        this.colum_type = colum_type;
    }

    public Integer getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(Integer unit_id) {
        this.unit_id = unit_id;
    }

    public String getColum_name() {
        return colum_name;
    }

    public void setColum_name(String colum_name) {
        this.colum_name = colum_name;
    }

    public String getColum_name_cn() {
        return colum_name_cn;
    }

    public void setColum_name_cn(String colum_name_cn) {
        this.colum_name_cn = colum_name_cn;
    }

    public Integer getMin_value() {
        return min_value;
    }

    public void setMin_value(Integer min_value) {
        this.min_value = min_value;
    }

    public Integer getMax_value() {
        return max_value;
    }

    public void setMax_value(Integer max_value) {
        this.max_value = max_value;
    }

    public String getColum_point() {
        return colum_point;
    }

    public void setColum_point(String colum_point) {
        this.colum_point = colum_point;
    }

    public String getColum_desc() {
        return colum_desc;
    }

    public void setColum_desc(String colum_desc) {
        this.colum_desc = colum_desc;
    }

    public String getColum_formula() {
        return colum_formula;
    }

    public void setColum_formula(String colum_formula) {
        this.colum_formula = colum_formula;
    }

    public String getColum_formula_desc() {
        return colum_formula_desc;
    }

    public void setColum_formula_desc(String colum_formula_desc) {
        this.colum_formula_desc = colum_formula_desc;
    }

    public String toString(){
        return JsonSupport.objectToJson(this);
    }
}
