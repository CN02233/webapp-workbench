package com.seaboxdata.cqny.record.entity.onedim;

public class ColumDefined {

    private Integer colum_id;
    private String colum_name;
    private String colum_name_cn;
    private Integer group_id;
    private String group_name;
    private Integer unit_id;
    private String colum_data_type;
    private Integer min_value;
    private Integer max_value    ;
    private String colum_formula    ;
    private String colum_formula_desc    ;


    public Integer getColum_id() {
        return colum_id;
    }

    public void setColum_id(Integer colum_id) {
        this.colum_id = colum_id;
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

    public Integer getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Integer group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public Integer getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(Integer unit_id) {
        this.unit_id = unit_id;
    }

    public String getColum_data_type() {
        return colum_data_type;
    }

    public void setColum_data_type(String colum_data_type) {
        this.colum_data_type = colum_data_type;
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
}