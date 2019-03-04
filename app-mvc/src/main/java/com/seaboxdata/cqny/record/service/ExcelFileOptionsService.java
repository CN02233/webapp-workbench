package com.seaboxdata.cqny.record.service;

import com.seaboxdata.cqny.record.entity.ExcelContext;
import com.seaboxdata.cqny.record.entity.ReportCell;

import java.io.File;
import java.util.List;

public interface ExcelFileOptionsService {

    List<ExcelContext> loadFileByFilePath(String filePath);

    List<List<List<ReportCell>>> loadReport(String reportIdOrName);

    File loadTemplateFileFromDisk(String templateName);
}
