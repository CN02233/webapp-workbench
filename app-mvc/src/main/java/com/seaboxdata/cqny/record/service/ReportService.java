package com.seaboxdata.cqny.record.service;

import com.seaboxdata.cqny.record.entity.ExcelContext;
import com.seaboxdata.cqny.record.entity.ReportCell;
import com.webapp.support.page.PageResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface ReportService {

    /**
     * 创建报表文件
     * @param templateIdOrName
     * @return
     */
    String createReport(String templateIdOrName,String reportName) throws IOException;

    /**
     * 获取报表数据
     * @param reportId
     * @return
     */
    List<List<List<ReportCell>>> loadReport(String reportId);

    List<ExcelContext> loadReportData(String reportID);

    PageResult reportList(int userId, int currPage, int pageSize);

    List<List<List<ReportCell>>> editSave(ArrayList<ReportCell> reportCells, String reportId);
}
