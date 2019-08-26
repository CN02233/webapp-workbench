package com.seaboxdata.cqny.record.entity.onedim;


import com.seaboxdata.cqny.record.entity.ColumDefined;

public class SimpleColumDefined extends ColumDefined {

    private Integer colum_id;
    private Integer group_id;
    private String group_name;
    private Integer parent_id;
    private String parent_name;

    private String need_remember;//是否需要记忆用户输入
    private String default_value;//参数默认值设置（发布时会读取该默认值作为初始化参数值）

    private Integer colum_order;
    private String colum_show;


    public Integer getColum_id() {
        return colum_id;
    }

    public void setColum_id(Integer colum_id) {
        this.colum_id = colum_id;
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

    public Integer getParent_id() {
        return parent_id;
    }

    public void setParent_id(Integer parent_id) {
        this.parent_id = parent_id;
    }

    public String getParent_name() {
        return parent_name;
    }

    public void setParent_name(String parent_name) {
        this.parent_name = parent_name;
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

    public String getColum_show() {
        return colum_show;
    }

    public void setColum_show(String colum_show) {
        this.colum_show = colum_show;
    }
}
