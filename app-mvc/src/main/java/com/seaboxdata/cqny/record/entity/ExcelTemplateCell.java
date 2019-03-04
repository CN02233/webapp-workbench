package com.seaboxdata.cqny.record.entity;

public class ExcelTemplateCell {

    private Integer template_id;
    private String template_name;
    private Integer template_row;
    private Integer template_col;
    private String template_context;
    private String template_col_styles;

    public Integer getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(Integer template_id) {
        this.template_id = template_id;
    }

    public String getTemplate_name() {
        return template_name;
    }

    public void setTemplate_name(String template_name) {
        this.template_name = template_name;
    }

    public Integer getTemplate_row() {
        return template_row;
    }

    public void setTemplate_row(Integer template_row) {
        this.template_row = template_row;
    }

    public Integer getTemplate_col() {
        return template_col;
    }

    public void setTemplate_col(Integer template_col) {
        this.template_col = template_col;
    }

    public String getTemplate_context() {
        return template_context;
    }

    public void setTemplate_context(String template_context) {
        this.template_context = template_context;
    }

    public String getTemplate_col_styles() {
        return template_col_styles;
    }

    public void setTemplate_col_styles(String template_col_styles) {
        this.template_col_styles = template_col_styles;
    }
}
