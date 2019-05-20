package com.seaboxdata.cqny.record.service;

import com.seaboxdata.cqny.record.entity.ReportDefinedEntity;
import com.seaboxdata.cqny.record.entity.SubmitReportRequestEntity;

import java.text.ParseException;

public interface SubmitReportService {

    void doSubmit(SubmitReportRequestEntity submitReportEntity) throws ParseException;

    void doSubmitForOrigins(SubmitReportRequestEntity submitReportEntity) throws ParseException;

}
