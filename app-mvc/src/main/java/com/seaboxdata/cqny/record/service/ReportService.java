package com.seaboxdata.cqny.record.service;

import com.seaboxdata.cqny.record.entity.ReportCell;
import com.seaboxdata.cqny.record.entity.ReportInfo;
import com.webapp.support.page.PageResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    ReportInfo loadReport(String reportId);

    ReportInfo loadReportData(String reportID);

    PageResult reportList(int userId, int currPage, int pageSize);

    void editSave(ArrayList<ArrayList<String>> reportCells, String reportId,String sheetId);

    void lockReport(String reportId,Integer userId);

    ReportInfo loadReportBasic(String reportId);

    void submitReport(String reportId, int userId);

    void reviewReport(String reportId, int userId);

    void confirmReport(String reportId, int userId);

    void fullEditSave(ArrayList<ArrayList<String>> reportCells, ArrayList<Map<String, String>> reportMerged, String reportId, String templateId,String sheetId);
}
