package com.seaboxdata.cqny.record.entity;

import com.webapp.support.json.JsonSupport;

import java.util.Date;
import java.util.List;

public class ExcelTemplate {

    private Integer template_id;

    private String template_name;

    private String template_source_file;

    private String source_file_name;

    private String import_user;

    private Date import_date;

    private List<ExcelTemplateSheet> excelTemplateSheets;



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

    public String getTemplate_source_file() {
        return template_source_file;
    }

    public void setTemplate_source_file(String template_source_file) {
        this.template_source_file = template_source_file;
    }

    public String getImport_user() {
        return import_user;
    }

    public void setImport_user(String import_user) {
        this.import_user = import_user;
    }

    public Date getImport_date() {
        return import_date;
    }

    public void setImport_date(Date import_date) {
        this.import_date = import_date;
    }

    public String getSource_file_name() {
        return source_file_name;
    }

    public void setSource_file_name(String source_file_name) {
        this.source_file_name = source_file_name;
    }

    public String toString(){
        return JsonSupport.objectToJson(this);
    }

    public List<ExcelTemplateSheet> getExcelTemplateSheets() {
        return excelTemplateSheets;
    }

    public void setExcelTemplateSheets(List<ExcelTemplateSheet> excelTemplateSheets) {
        this.excelTemplateSheets = excelTemplateSheets;
    }
}
