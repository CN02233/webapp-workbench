package com.seaboxdata.cqny.record.entity;

import org.springframework.web.multipart.MultipartFile;

public class TemplateImport {

    private String tempalteName;

    private MultipartFile templateFile;

    public String getTempalteName() {
        return tempalteName;
    }

    public void setTempalteName(String tempalteName) {
        this.tempalteName = tempalteName;
    }

    public MultipartFile getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(MultipartFile templateFile) {
        this.templateFile = templateFile;
    }
}
