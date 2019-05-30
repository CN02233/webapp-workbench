package com.crawler.webapp.job.bean;

public class JobSchedule {
    private Integer job_schedule_id;
    private Integer job_schedule_type;
    private JobScheduleParam jobScheduleParam;

    public Integer getJob_schedule_id() {
        return job_schedule_id;
    }

    public void setJob_schedule_id(Integer job_schedule_id) {
        this.job_schedule_id = job_schedule_id;
    }

    public Integer getJob_schedule_type() {
        return job_schedule_type;
    }

    public void setJob_schedule_type(Integer job_schedule_type) {
        this.job_schedule_type = job_schedule_type;
    }

    public JobScheduleParam getJobScheduleParam() {
        return jobScheduleParam;
    }

    public void setJobScheduleParam(JobScheduleParam jobScheduleParam) {
        this.jobScheduleParam = jobScheduleParam;
    }
}
