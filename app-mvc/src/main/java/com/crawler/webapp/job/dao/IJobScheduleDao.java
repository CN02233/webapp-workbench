package com.crawler.webapp.job.dao;

import com.crawler.webapp.job.bean.CrawlerConfig;
import com.crawler.webapp.job.bean.JobSchedule;
import com.crawler.webapp.job.bean.JobScheduleParam;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

/**
 * Created by SongCQ on 2017/10/10.
 */
public interface IJobScheduleDao {

    @Select("select *,job_schedule_id as jscheduleId from job_schedule js ")
   @Results({
            @Result(property = "jobScheduleParam", column = "jscheduleId",
                    many = @Many(select="com.crawler.webapp.job.dao.IJobScheduleDao.getJobScheduleParam"))
    })
    @Options(useCache = false)
    Page<JobSchedule> pagingJobScheduleList(@Param("currPage") int currPage, @Param("pageSize") int pageSize);

    @Select("select jsp.* from job_schedule_param jsp where job_schedule_id = #{job_schedule_id} limit 1")
    @Options(useCache = false)
    JobScheduleParam getJobScheduleParam(int job_schedule_id);

    @Insert("insert into job_schedule (job_schedule_id,job_schedule_type) values (#{job_schedule_id},#{job_schedule_type})")
    void saveJobSchedule(JobSchedule jobSchedule);

    @Insert("insert into job_schedule_param (job_schedule_id,param_name,param_value) values (#{job_schedule_id},#{param_name},#{param_value})")
    void saveNewScheduleParam(JobScheduleParam jobScheduleParam);

    @Select("select *,job_schedule_id as jscheduleId from job_schedule where job_schedule_id = #{id}" )
    @Results({
            @Result(property = "jobScheduleParam", column = "jscheduleId",
                    many = @Many(select="com.crawler.webapp.job.dao.IJobScheduleDao.getJobScheduleParam"))
    })
    @Options(useCache = false)
    JobSchedule getScheduleInfo(@Param("id") int job_schedule_id);

    @Update("update job_schedule js,job_schedule_param jsp set js.job_schedule_id = #{sId},js.job_schedule_type = #{type}," +
            "jsp.job_schedule_id = #{sId},jsp.param_name = #{Pname},jsp.param_value = #{Pvalue} " +
            "where js.job_schedule_id = jsp.job_schedule_id and js.job_schedule_id = #{id}")
    void updateScheduleAndSceduleParam(@Param("sId") int job_schedule_id,@Param("type") int job_schedule_type,@Param("Pname") String param_name,@Param("Pvalue") String param_value,@Param("id") int schedule_id);

    @Delete("delete js,jsp from job_schedule js inner join job_schedule_param jsp on jsp.job_schedule_id = js.job_schedule_id where js.job_schedule_id = #{id}")
    void delSchedule(@Param("id") int job_schedule_id);

}
