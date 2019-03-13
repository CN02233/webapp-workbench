package com.seaboxdata.cqny.record.entity;

public class ExcelReportCell {
    private Integer id;
    private Integer report_id;
    private Integer template_id;
    private Integer template_sheet_id;
    private Integer report_row;
    private Integer report_colum;
    private String report_context;
    private String context_script;
    private String context_readonly;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReport_id() {
        return report_id;
    }

    public void setReport_id(Integer report_id) {
        this.report_id = report_id;
    }

    public Integer getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(Integer template_id) {
        this.template_id = template_id;
    }

    public Integer getTemplate_sheet_id() {
        return template_sheet_id;
    }

    public void setTemplate_sheet_id(Integer template_sheet_id) {
        this.template_sheet_id = template_sheet_id;
    }

    public Integer getReport_row() {
        return report_row;
    }

    public void setReport_row(Integer report_row) {
        this.report_row = report_row;
    }

    public Integer getReport_colum() {
        return report_colum;
    }

    public void setReport_colum(Integer report_colum) {
        this.report_colum = report_colum;
    }

    public String getReport_context() {
        return report_context;
    }

    public void setReport_context(String report_context) {
        this.report_context = report_context;
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
