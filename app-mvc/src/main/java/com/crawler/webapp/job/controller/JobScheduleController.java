package com.crawler.webapp.job.controller;

import com.crawler.webapp.job.bean.JobSchedule;
import com.crawler.webapp.job.bean.JobScheduleParam;
import com.crawler.webapp.job.service.JobScheduleService;
import com.github.pagehelper.Page;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("job/jobSchedule")
public class JobScheduleController {

    private Logger logger = LoggerFactory.getLogger(JobScheduleController.class);

    @Autowired
    private JobScheduleService jobScheduleService;

    @RequestMapping("pagingList")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public String pagingJobScheduleList(int currPage,int pageSize){
        Page<JobSchedule> crawListPage = jobScheduleService.pagingJobScheduleList(currPage, pageSize);
        PageResult pageResult = PageResult.pageHelperList2PageResult(crawListPage);
        String result = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS,"获取成功",null,pageResult);
        logger.debug("paging crawl list result :{}",result);
        return result;
    }


    @RequestMapping("saveNewSchedule")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public String saveJobSchedule(@RequestBody JobSchedule jobSchedule){
        jobScheduleService.saveJobSchedule(jobSchedule);
        String result = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS,"保存成功",null,null);
        return  result;
    }

   @RequestMapping("saveNewScheduleParam")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public String saveNewScheduleParam(@RequestBody JobScheduleParam jobScheduleParam) {
       int job_schedule_id = jobScheduleParam.getJob_schedule_id();
       JobScheduleParam ScheduleParam = jobScheduleService.getJobScheduleParam(job_schedule_id);
       if (ScheduleParam == null) {
             //job_schedule_id未存在
             jobScheduleService.saveNewScheduleParam(jobScheduleParam);
       }
       String result = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS,"保存成功",null,null);
        return  result;
    }

    @RequestMapping("getScheduleInfo")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public String getScheduleInfo(int job_schedule_id){
        JobSchedule scheduleInfo = jobScheduleService.getScheduleInfo(job_schedule_id);
        Map<String,Object> resultMap = new HashMap();
        resultMap.put("scheduleInfo",scheduleInfo);
        String result = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS,"保存成功",null,resultMap);
        return result;
    }


    @RequestMapping("updateSchedule")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public String updateSchedule(String param_name,String param_value,int job_schedule_id,int job_schedule_type,int schedule_id)throws Exception{
        param_name = strToUtf8(param_name);
        param_value = strToUtf8(param_value);
        jobScheduleService.updateScheduleAndSceduleParam(job_schedule_id,job_schedule_type,param_name,param_value,schedule_id);
        String result = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS,"保存成功",null,null);
        return  result;
    }

    @RequestMapping("delSchedule")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public String delSchedule(int job_schedule_id)throws Exception{
         jobScheduleService.delSchedule(job_schedule_id);
        String result = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS,"删除成功",null,null);
        return result;
    }

    public String strToUtf8(String str)throws Exception{
        str = new String(str.getBytes("iso-8859-1"),"utf-8");
        return str;
    }
}
