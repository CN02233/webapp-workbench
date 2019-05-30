package com.crawler.webapp.job.service.imp;

import com.crawler.webapp.job.bean.CrawlerConfig;
import com.crawler.webapp.job.bean.JobSchedule;
import com.crawler.webapp.job.bean.JobScheduleParam;
import com.crawler.webapp.job.dao.IJobScheduleDao;
import com.crawler.webapp.job.service.JobScheduleService;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by SongCQ on 2017/10/10.
 */
@Service("jobScheduleService")
public class JobScheduleServiceImp implements JobScheduleService {

    @Autowired(required = false)
    private IJobScheduleDao iJobScheduleDao;

    @Override
    public Page<JobSchedule> pagingJobScheduleList(int currPage, int pageSize) {
        Page<JobSchedule> result = iJobScheduleDao.pagingJobScheduleList(currPage, pageSize);
        return result;
    }

    @Override
    public JobScheduleParam getJobScheduleParam(int job_schedule_id) {
        return iJobScheduleDao.getJobScheduleParam(job_schedule_id);
    }

    @Override
    public void saveJobSchedule(JobSchedule jobSchedule) {
        iJobScheduleDao.saveJobSchedule(jobSchedule);
    }

    @Override
    public void saveNewScheduleParam(JobScheduleParam jobScheduleParam) {
        iJobScheduleDao.saveNewScheduleParam(jobScheduleParam);
    }

    @Override
    public JobSchedule getScheduleInfo(int job_schedule_id) {
        return iJobScheduleDao.getScheduleInfo(job_schedule_id);
    }

    @Override
    public void updateScheduleAndSceduleParam(int job_schedule_id,int job_schedule_type,String param_name,String param_value,int schedule_id) {
           iJobScheduleDao.updateScheduleAndSceduleParam(job_schedule_id,job_schedule_type,param_name,param_value,schedule_id);
    }


    @Override
    public void delSchedule(int job_schedule_id) {
        iJobScheduleDao.delSchedule(job_schedule_id);
    }

}
