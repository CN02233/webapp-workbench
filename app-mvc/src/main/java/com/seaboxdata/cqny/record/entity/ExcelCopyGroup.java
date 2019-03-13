package com.seaboxdata.cqny.record.entity;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class ExcelCopyGroup {
    private Integer id;
    private Integer template_id;
    private Integer sheet_id;
    private String group_name;
    private String group_rows;
    private ArrayList<String> group_list;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(Integer template_id) {
        this.template_id = template_id;
    }

    public Integer getSheet_id() {
        return sheet_id;
    }

    public void setSheet_id(Integer sheet_id) {
        this.sheet_id = sheet_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_rows() {
        return group_rows;
    }

    public void setGroup_rows(String group_rows) {
        this.group_rows = group_rows;
        String groupRows = group_rows.replace("[","").replace("]","");
        String[] listStr = groupRows.split(",");
        this.group_list = Lists.newArrayList(listStr);
    }

    public List<String> getGroup_list() {
        return group_list;
    }

    public void setGroup_list(ArrayList<String> group_list) {
        this.group_list = group_list;
    }
}
