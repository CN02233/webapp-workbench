package com.crawler.webapp.job.bean;

public class JobScheduleParam {

    private Integer job_schedule_id;
    private Integer job_schedule_type;
    private String  param_name;
    private String  param_value;

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

    public String getParam_name() {
        return param_name;
    }

    public void setParam_name(String param_name) {
        this.param_name = param_name;
    }

    public String getParam_value() {
        return param_value;
    }

    public void setParam_value(String param_value) {
        this.param_value = param_value;
    }
}
