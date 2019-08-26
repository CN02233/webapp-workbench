package com.seaboxdata.cqny.record.entity.onedim;


import com.seaboxdata.cqny.record.entity.ColumDefined;

public class GridColumDefined extends ColumDefined {

    private Integer colum_id;
    private Integer dim_id;
    private String dim_name;
    private String dim_name_cn;
    private String colum_id_no;
    private String dim_id_no;
    private String colum_meta_type;
    private String need_remember;//是否需要记忆用户输入

    private String default_value;

    private Integer colum_order;

    public Integer getColum_id() {
        return colum_id;
    }

    public void setColum_id(Integer colum_id) {
        this.colum_id = colum_id;
    }

    public Integer getDim_id() {
        return dim_id;
    }

    public void setDim_id(Integer dim_id) {
        this.dim_id = dim_id;
    }

    public String getDim_name() {
        return dim_name;
    }

    public void setDim_name(String dim_name) {
        this.dim_name = dim_name;
    }

    public String getDim_name_cn() {
        return dim_name_cn;
    }

    public void setDim_name_cn(String dim_name_cn) {
        this.dim_name_cn = dim_name_cn;
    }

    public String getColum_id_no() {
        return colum_id_no;
    }

    public void setColum_id_no(String colum_id_no) {
        this.colum_id_no = colum_id_no;
    }

    public String getDim_id_no() {
        return dim_id_no;
    }

    public void setDim_id_no(String dim_id_no) {
        this.dim_id_no = dim_id_no;
    }

    public String getColum_meta_type() {
        return colum_meta_type;
    }

    public void setColum_meta_type(String colum_meta_type) {
        this.colum_meta_type = colum_meta_type;
    }

    public String getNeed_remember() {
        return need_remember;
    }

    public void setNeed_remember(String need_remember) {
        this.need_remember = need_remember;
    }

    public String getDefault_value() {
        return default_value;
    }

    public void setDefault_value(String default_value) {
        this.default_value = default_value;
    }

    public Integer getColum_order() {
        return colum_order;
    }

    public void setColum_order(Integer colum_order) {
        this.colum_order = colum_order;
    }
}
