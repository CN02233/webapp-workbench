package com.seaboxdata.cqny.record.service;

import com.seaboxdata.cqny.record.entity.ExcelTemplate;
import com.webapp.support.page.PageResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface TemplateService {

    /**
     * 获取报表模板数据
     * @param templateId 模板ID
     * @return
     */
    ExcelTemplate loadTemplate(String templateId);

    List<ExcelTemplate> loadTemplateBasicInfo(String templateId);

    String uploadTemplate(String tempalteName,String originId, File uploadFile) throws IOException;

    PageResult pageTempaltes(int currPage, int pageSize);

    List<ExcelTemplate> getTemplatesByUser(int user_id);

    void editSaveTemplate(String templateId, String sheetId, ArrayList<ArrayList<String>> templateContext, ArrayList<HashMap<String,Object>> copyGroups);

    void templteDelete(String templateId);
}
