package com.seaboxdata.cqny.record.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.Holiday;
import com.seaboxdata.cqny.record.entity.ReportCustomer;
import com.seaboxdata.cqny.record.entity.ReportSmsConfig;
import com.workbench.auth.user.entity.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IReportSmsDao {

    @Select("select * from china_holidays")
    List<Holiday> getHolidays();

    @Select("select " +
            "id," +
            "config_name," +
            "report_defined_id," +
            "sms_template_id," +
            "send_date," +
            "send_time," +
            "pre_days," +
            "cross_holiday," +
            "send_status" +
            " from report_sms_config where send_time <= #{currDate} and (send_status=0 or send_status=3)")
    List<ReportSmsConfig> getTodayReportSmsConfigs(String currDate);

    @Insert("insert into report_sms_config (" +
            "id," +
            "config_name," +
            "report_defined_id," +
            "sms_template_id," +
            "send_date," +
            "send_time," +
            "pre_days," +
            "cross_holiday," +
            "send_status) values (" +
            "#{id}," +
            "#{config_name}," +
            "#{report_defined_id}," +
            "#{sms_template_id}," +
            "#{send_date}," +
            "#{send_time}," +
            "#{pre_days}," +
            "#{cross_holiday}," +
            "'0')")
    void saveSmsJob(ReportSmsConfig reportSmsConfig);

    @Update("update report_sms_config set send_status = #{status} where id = #{configId}")
    void changeJobStatus(@Param("configId") Integer configId,@Param("status") String status);

    @Select("select so.origin_id,so.origin_name,u.* from report_defined_origin_assign rdoa,sys_origin so ,user_origin_assign uoa ," +
            "user u  where rdoa.defined_id='26' and rdoa.origin_id = so.origin_id and " +
            "so.origin_id = uoa.origin_id and uoa.user_id = u.user_id")
    List<User> getReportUsers(String reportDefinedId);

    @Select("select rc.report_origin,count(0) report_count from report_customer rc where rc.report_defined_id = #{reportDefinedId} " +
            "and report_end_date >= #{sendDate} and report_status='0' group by rc.report_origin")
    List<Map<String,Object>> getReportCount4Origin(@Param("reportDefinedId") String reportDefinedId, @Param("sendDate") String sendDate);


    @Select("select " +
            "rsc.id," +
            "rsc.config_name," +
            "rsc.report_defined_id," +
            "rsc.sms_template_id," +
            "rsc.send_date," +
            "rsc.send_time," +
            "rsc.pre_days," +
            "rsc.cross_holiday," +
            "rsc.send_status," +
            "rd.defined_name report_defined_name" +
            " from report_sms_config rsc,report_defined rd where rsc.report_defined_id = rd.defined_id")
    Page<ReportSmsConfig> pageSms(@Param("currPage") Integer currPage,@Param("pageSize") Integer pageSize);

    @Select("select * from ali_sms_templates")
    List<Map<String, Object>> getAliSmsTemplates();

    @Select("select * from report_customer where report_defined_id = ${report_defined_id} and report_status='0'")
    List<ReportCustomer> checkReportCount(@Param("report_defined_id") String report_defined_id);

    @Delete("delete from report_sms_config where id = #{smsId}")
    void deleteSmsConfig(@Param("smsId") String smsId);

    @Insert("insert into send_sms_record (sms_config_id,sms_config_name,phone_number,cust_name,send_result,faild_reason,send_context) " +
            "values (#{sms_config_id},#{sms_config_name},#{phone_number},#{cust_name},#{send_result},#{faild_reason},#{send_context})")
    void recordSmsSend(Map<String, String> sendParams);
}
