package com.seaboxdata.cqny.record.entity;

import com.webapp.support.json.JsonSupport;

import java.util.List;

public class ExcelTemplateCell {

    private Integer id;
    private Integer sheet_id;
    private Integer template_row;
    private Integer template_col;
    private String template_context;
    private String template_col_styles;
    private String context_script;
    private String context_readonly;

    private List<ExcelTemplateCellMerged> excelTemplateCellMerged;

    public Integer getSheet_id() {
        return sheet_id;
    }

    public void setSheet_id(Integer sheet_id) {
        this.sheet_id = sheet_id;
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


    public List<ExcelTemplateCellMerged> getExcelTemplateCellMerged() {
        return excelTemplateCellMerged;
    }

    public void setExcelTemplateCellMerged(List<ExcelTemplateCellMerged> excelTemplateCellMerged) {
        this.excelTemplateCellMerged = excelTemplateCellMerged;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String toString(){
        return JsonSupport.objectToJson(this);
    }

    public String getContext_script() {
        return context_script;
    }

    public void setContext_script(String context_script) {
        this.context_script = context_script;
    }

    public String getContext_readonly() {
        return context_readonly;
    }

    public void setContext_readonly(String context_readonly) {
        this.context_readonly = context_readonly;
    }
}
