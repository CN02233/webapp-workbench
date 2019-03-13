package com.seaboxdata.cqny.record.service.imp;

import com.AbstractTestService;
import com.github.pagehelper.Page;
import com.googlecode.aviator.AviatorEvaluator;
import com.seaboxdata.cqny.record.entity.ExcelContext;
import com.seaboxdata.cqny.record.entity.ReportCell;
import com.seaboxdata.cqny.record.entity.ReportInfo;
import com.seaboxdata.cqny.record.service.ReportService;
import com.webapp.support.page.PageResult;
import junit.framework.TestCase;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReportServiceImpTest extends AbstractTestService {

    @Resource
    private ReportService reportService;

    @Test
    public void testLoadReport() {
        ReportInfo reportInfo = reportService.loadReport("2");
        System.out.println(reportInfo);
    }

    @Test
    public void testCreateReport() throws IOException {
        String createResult = reportService.createReport("1", "测试报表111");
        System.out.println(createResult);
    }

    @Test
    public void testReportList() {
        PageResult result = reportService.reportList(2, 2, 20);
        System.out.println(result);
    }

    @Test
    public void testEditSave() {
//        List<ReportCell> reportCells = new ArrayList(10);
//        for(int i=0;i<10;i++){
//            ReportCell reportCell = new ReportCell();
//            reportCell.setRow(0);
//            reportCell.setColumn(i);
//            reportCell.setVal(""+i);
//            reportCells.add(reportCell);
//        }
//        reportService.editSave(reportCells,"9");
    }

    @Test
    public void testLoadReportData() {
        ReportInfo result = reportService.loadReportData("2");
        System.out.println(result);
    }

    @Test
    public void testLockReport() {
        reportService.lockReport("9",1  );
    }

    @Test
    public void main(){
        Long result = (Long) AviatorEvaluator.execute("1+2+3");
        System.out.println(result);
    }
}