package com.crawler.webapp.job.service;

import com.crawler.webapp.job.bean.JobSchedule;
import com.crawler.webapp.job.bean.JobScheduleParam;
import com.github.pagehelper.Page;

/**
 * Created by SongCQ on 2017/10/10.
 */
public interface JobScheduleService {
    Page<JobSchedule> pagingJobScheduleList(int currPage, int pageSize);

    JobScheduleParam getJobScheduleParam(int job_schedule_id);

    void saveJobSchedule(JobSchedule jobSchedule);

    void saveNewScheduleParam(JobScheduleParam jobScheduleParam);

    JobSchedule getScheduleInfo(int job_schedule_id);

    void updateScheduleAndSceduleParam(int job_schedule_id,int job_schedule_type,String param_name,String param_value,int schedule_id);

    void delSchedule(int job_schedule_id);


}
