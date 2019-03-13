package com.seaboxdata.cqny.record.entity;

import org.springframework.web.multipart.MultipartFile;

public class TemplateImport {

    private String templateName;

    private String originId;

    private MultipartFile templateFile;


    public MultipartFile getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(MultipartFile templateFile) {
        this.templateFile = templateFile;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }
}
