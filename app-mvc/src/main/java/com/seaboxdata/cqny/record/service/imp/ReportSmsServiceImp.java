package com.seaboxdata.cqny.record.service.imp;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.github.pagehelper.Page;
import com.google.common.base.Strings;
import com.seaboxdata.cqny.record.config.AliSmsConfig;
import com.seaboxdata.cqny.record.dao.IReportSmsDao;
import com.seaboxdata.cqny.record.entity.Holiday;
import com.seaboxdata.cqny.record.entity.ReportCustomer;
import com.seaboxdata.cqny.record.entity.ReportSmsConfig;
import com.seaboxdata.cqny.record.service.ReportCustomerService;
import com.seaboxdata.cqny.record.service.ReportSmsService;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.page.PageResult;
import com.workbench.auth.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service("reportSmsService")
public class ReportSmsServiceImp implements ReportSmsService {

    private final static String SEND_SUCCESS = "OK";

    private Logger logger = LoggerFactory.getLogger(ReportSmsServiceImp.class);

    @Autowired
    private ReportCustomerService reportCustomerService;

    @Autowired
    private IReportSmsDao reportSmsDao;

    @Autowired
    private AliSmsConfig aliSmsConfig;

    private Map<String,ReportSmsConfig> reportSmsJobs = new HashMap<>();

    @PostConstruct
    public void initialize(){
        //获取当天所有的待执行任务
        this.listenSendJobs();
    }

    private void listenSendJobs(){

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<ReportSmsConfig> sendJobs = checkJobsToday();

                logger.info("开始执行发送短信提醒任务,待发送任务数量{}",sendJobs!=null?sendJobs.size():0);

                if(sendJobs!=null){
                    doSend(sendJobs);
                }
            }
        };

        ScheduledExecutorService exceutor = Executors.newSingleThreadScheduledExecutor();
        exceutor.scheduleAtFixedRate(runnable,40,180,TimeUnit.SECONDS);
    }

    @Transactional(rollbackFor = Exception.class)
    public void doSend(List<ReportSmsConfig> sendJobs){
        for (ReportSmsConfig sendJob : sendJobs) {
            Date sendTime = sendJob.getSend_time();

            logger.info("发送.....{}",sendJob.getConfig_name());
            logger.info("开始修改发送状态{}",sendJob.getConfig_name());
            changeJobStatus(sendJob.getId(),"1");//修改发送状态为发送中
            logger.debug("修改发送状态完成{}",sendJob.getConfig_name());
            sendSmsForCustomer(sendJob,sendTime);
            logger.info("开始修改发送状态为完成..{}",sendJob.getConfig_name());
            changeJobStatus(sendJob.getId(),"2");//修改发送状态为发送完毕
            logger.debug("修改发送状态完成{}",sendJob.getConfig_name());
        }
        logger.info("短信提醒发送完毕");
    }

    private List<ReportSmsConfig> checkJobsToday(){
        List<ReportSmsConfig> allSmsConfigs = reportSmsDao.getTodayReportSmsConfigs(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
        return allSmsConfigs;
    }

    @Override
    public PageResult pagerSms(Integer currPage, Integer pageSize) {
        Page<ReportSmsConfig> reportSmsConfigPage = reportSmsDao.pageSms(currPage,pageSize);
        PageResult pageResult = PageResult.pageHelperList2PageResult(reportSmsConfigPage);
        return pageResult;
    }

    @Override
    public String createSmsJob(ReportSmsConfig reportSmsConfig) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());


        List<ReportCustomer> reportCustomers = reportSmsDao.checkReportCount(reportSmsConfig.getReport_defined_id());
        Date reportDataEnd = null;
        if(reportCustomers!=null&&reportCustomers.size()>0){
            ReportCustomer reportCust = reportCustomers.get(0);
            reportDataEnd = reportCust.getReport_data_end();
            if(calendar.after(reportDataEnd)){
                return "所有报表都已过期";
            }
        }
        else{
            return "当前报表定义下无待填报报表";
        }

        this.checkSendDate(reportSmsConfig,reportDataEnd);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");

        Calendar sendDateCal = Calendar.getInstance();
        sendDateCal.setTime(reportSmsConfig.getSend_date());
        if(calendar.after(sendDateCal)){
            boolean sameDay = simpleDate.format(calendar.getTime()).equals(simpleDate.format(sendDateCal.getTime()));
            if(sameDay){
                StringBuilder sendTImeSB = new StringBuilder();
                sendTImeSB.append(reportSmsConfig.getSend_date_str()).append(" ").append(reportSmsConfig.getSend_time_str());
                SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Calendar sendTimeCal = Calendar.getInstance();
                try {
                    sendTimeCal.setTime(timeFormat.parse(sendTImeSB.toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(calendar.after(sendTimeCal)){
                    return "发送时间"+sendTImeSB.toString()+ "在当前日期之前,设置失败";
                }
            }else{
                return "发送日期"+simpleDate.format(reportSmsConfig.getSend_date())+"在当前时间之前,设置失败";
            }
        }


        String sendDateStr = simpleDate.format(reportSmsConfig.getSend_date());
        logger.debug("发送日期为{}",sendDateStr);

        StringBuilder sendTImeSB = new StringBuilder();
        sendTImeSB.append(sendDateStr).append(" ").append(reportSmsConfig.getSend_time_str());

        try {
            reportSmsConfig.setSend_time(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(sendTImeSB.toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        reportSmsDao.saveSmsJob(reportSmsConfig);

        return null;
    }

    private void checkSendDate(ReportSmsConfig reportSmsConfig,Date reportDataEnd){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String preDays = reportSmsConfig.getPre_days();
        Integer preDaysInt = new Integer(preDays);
        String crossHoliday = reportSmsConfig.getCross_holiday();
        String reportDefinedID = reportSmsConfig.getReport_defined_id();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(reportDataEnd);
        if(!Strings.isNullOrEmpty(crossHoliday)&&"N".equals(crossHoliday)){
            calendar.add(Calendar.HOUR,preDaysInt*(-24));
            Date senddate = calendar.getTime();
            String sendDateStr = format.format(senddate);
            logger.debug("send date is {}",sendDateStr);
            reportSmsConfig.setSend_date(senddate);
        }else{
            List<Holiday> allHolidays = reportSmsDao.getHolidays();
            List<String> holidayStrList = new ArrayList<>();

            for (Holiday holidayObj : allHolidays) {
                holidayObj.getHoliday_date();
                holidayStrList.add(format.format(holidayObj.getHoliday_date()));
            }

            for(int index=0;index<preDaysInt;index++){
                calendar.add(Calendar.HOUR,-24);
                if(holidayStrList.contains(format.format(calendar.getTime()))){
                    index--;
                    logger.debug("跳过日期{}，当前为假期",format.format(calendar.getTime()));
                }else{
                    if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ||
                            calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
                        index--;
                        logger.debug("跳过日期{}，当前为周末",format.format(calendar.getTime()));
                    }
                }
            }
            Date senddate = calendar.getTime();
            reportSmsConfig.setSend_date(senddate);

        }

    }

    @Override
    public String queryTemplateContext(String templateId) {

        String responseData = this.doSmsProcess("QuerySmsTemplate",templateId, new HashMap(),null);

        return responseData;
    }

    @Override
    public String sendSmsForCustomer(ReportSmsConfig sendJob,Date sendTime) {
        List<User> userList = reportSmsDao.getReportUsers(sendJob.getReport_defined_id());
        List<Map<String, Object>> reportCountData = reportSmsDao.getReportCount4Origin(sendJob.getReport_defined_id(),
                new SimpleDateFormat("yyyy-MM-dd").format(sendTime));

        Map<Integer,Long> originCountTMp = new HashMap<>();
        for (Map<String, Object> reportCountDatum : reportCountData) {
            Integer reportOriginId = (Integer) reportCountDatum.get("report_origin");
            Long reportCount = (Long) reportCountDatum.get("report_count");
            originCountTMp.put(reportOriginId,reportCount);
        }

        for (User user : userList) {
            Integer userOriginId = user.getOrigin_id();
            String phoneNo = user.getMobile_phone();
            Map<String,Object> sendParams = new HashMap<>();
            sendParams.put("count",originCountTMp.get(userOriginId));
            sendParams.put("dueDate",new SimpleDateFormat("yyyy-MM-dd").format(sendTime));

            Map<String,String> sendRecordMap = new HashMap<>();
            sendRecordMap.put("sms_config_id",String.valueOf(sendJob.getId()));
            sendRecordMap.put("sms_config_name",sendJob.getConfig_name());
            sendRecordMap.put("phone_number",phoneNo);
            sendRecordMap.put("cust_name",user.getUser_name_cn());
            if(!Strings.isNullOrEmpty(phoneNo)){
                String response = this.doSmsProcess("SendSms",sendJob.getSms_template_id(), sendParams,phoneNo);
                if(!Strings.isNullOrEmpty(response)){
                    HashMap responseMap = JsonSupport.jsonToMap(response);
                    if(responseMap.containsKey("Code")){
                        String aliRespnseCode = (String) responseMap.get("Code");

                        String sendContext = JsonSupport.objectToJson(sendParams);
                        sendRecordMap.put("send_context",sendContext);

                        if(!"OK".equals(aliRespnseCode)){
                            sendRecordMap.put("send_result","FAILED");
//                        JsonSupport.objectToJson(response)
                            sendRecordMap.put("faild_reason",response);
                        }else{
                            sendRecordMap.put("send_result","SUCCESS");
                        }

                    }
                }
                logger.debug("发送信息结果:{}",response);
            }else{
                String sendContext = JsonSupport.objectToJson(sendParams);
                sendRecordMap.put("send_context",sendContext);
                sendRecordMap.put("send_result","FAILED");
                sendRecordMap.put("faild_reason","手机号为空");
            }

            reportSmsDao.recordSmsSend(sendRecordMap);
//            logger.debug("向电话号{},发送短信{}",phoneNo,sendParams.toString());
        }

        return null;
    }

    @Override
    public List<Map<String, Object>> getAliSmsTemplates() {
        List<Map<String, Object>> tempaltes = reportSmsDao.getAliSmsTemplates();
        return tempaltes;
    }

    @Override
    public void deleteSmsJob(String smsId) {
        reportSmsDao.deleteSmsConfig(smsId);
    }


    @Override
    public String doSmsProcess(String processName,String smsTemplatId,Map<String,Object> sendParams,String phoneNum){
        DefaultProfile profile = DefaultProfile.getProfile("default", aliSmsConfig.getAccessKeyId(), aliSmsConfig.getSecret());
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        request.setDomain(aliSmsConfig.getDoMain());
        request.setVersion(aliSmsConfig.getVersion());
        request.setAction(processName);
        String signName = aliSmsConfig.getSignName();
        request.putQueryParameter("SignName", signName);
//        request.putQueryParameter("SignName", "重庆天然气交易中心签名");
        request.putQueryParameter("TemplateCode",smsTemplatId);
        request.putQueryParameter("PhoneNumbers", phoneNum);

        if(sendParams!=null&&sendParams.size()>0){
            request.putQueryParameter("TemplateParam", JsonSupport.objectToJson(sendParams));
        }

        sendParams.put("SignName",signName);
        sendParams.put("TemplateCode",smsTemplatId);
        sendParams.put("PhoneNumbers",phoneNum);

        try {
            CommonResponse response = client.getCommonResponse(request);

//            System.out.println(response.getData());
            return response.getData();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return null;
    }



    public void changeJobStatus(Integer configId,String status) {
        reportSmsDao.changeJobStatus(configId,status);
    }
}
