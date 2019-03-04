package com.seaboxdata.cqny.record.service;

import com.seaboxdata.cqny.record.entity.ExcelTemplate;
import com.webapp.support.page.PageResult;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface TemplateService {

    /**
     * 获取报表模板数据
     * @param templateId 模板ID
     * @return
     */
    List<ExcelTemplate> loadTemplate(String templateId);

    List<ExcelTemplate> loadTemplateBasicInfo(String templateId);

    String uploadTemplate(String tempalteName, File uploadFile) throws IOException;

    PageResult pageTempaltes(int currPage, int pageSize);
}
