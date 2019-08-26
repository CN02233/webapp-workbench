package com.seaboxdata.cqny.record.service;

import com.seaboxdata.cqny.record.entity.ReportSmsConfig;
import com.webapp.support.page.PageResult;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ReportSmsService {

    PageResult pagerSms(Integer currPage, Integer pageSize);

    String createSmsJob(ReportSmsConfig reportSmsConfig);

    String queryTemplateContext(String templateId);

    String sendSmsForCustomer(ReportSmsConfig sendJob,Date sendTime);

    List<Map<String, Object>> getAliSmsTemplates();

    void deleteSmsJob(String smsId);

    String doSmsProcess(String processName,String smsTemplatId,Map<String,Object> sendParams,String phoneNum);
}
