package com.seaboxdata.cqny.record.service;

import com.seaboxdata.cqny.record.entity.ReportCell;

import java.util.List;

public interface ReportService {

    /**
     * 创建报表文件
     * @param templateIdOrName
     * @return
     */
    String createReport(String templateIdOrName);

    /**
     * 获取报表数据
     * @param reportId
     * @return
     */
    List<List<ReportCell>> loadReport(String reportId);

}
