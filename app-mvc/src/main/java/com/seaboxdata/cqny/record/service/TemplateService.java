package com.seaboxdata.cqny.record.service;

import com.webapp.support.jsonp.JsonResult;

import java.util.List;
import java.util.Map;

public interface TemplateService {

    /**
     * 获取报表模板数据
     * @param templateId 模板ID
     * @return
     */
    List<Map<String,String>> loadTemplate(String templateId);

}
