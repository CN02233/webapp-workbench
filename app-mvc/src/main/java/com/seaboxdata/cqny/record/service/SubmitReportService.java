package com.seaboxdata.cqny.record.service;

import com.seaboxdata.cqny.record.entity.SubmitReportRequestEntity;

import java.text.ParseException;

public interface SubmitReportService {

    void doSubmit(SubmitReportRequestEntity submitReportEntity) throws ParseException;

}
