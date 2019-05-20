package com.crawler.webapp.job.dao;

import com.crawler.webapp.job.bean.CrawlerConfig;
import com.crawler.webapp.job.bean.JobInfoBean;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

/**
 * Created by SongCQ on 2017/10/10.
 */
public interface IJobConfigDao {

    @Select("<script>" +
            "SELECT user_id,user_id as user_id_cp,job_id,job_id as job_id_cp,param_name,param_value FROM crawl_config  " +
            "</script>")
    @Results({
            @Result(property = "job_info",column = "job_id_cp",
                    many = @Many(select="com.crawler.webapp.job.dao.IJobMgDao.getCrawlData")),
            @Result(property = "user_info",column = "user_id_cp",
                    many = @Many(select="com.workbench.auth.user.dao.IUserServiceDao.getUserByUserId"))})
    @Options(useCache = false)
    Page<CrawlerConfig> pagingCrawlConfigList(@Param("currPage") int currPage, @Param("pageSize") int pageSize);


    @Insert("insert into crawl_config (user_id,job_id,param_name,param_value) values (#{user_id},#{job_id},#{param_name},#{param_value})")
    void saveCrawlConfig(CrawlerConfig crawlerConfig);

    @Delete("delete from crawl_config where user_id=#{user_id} and job_id=#{job_id} and param_name=#{param_name} and param_value=#{param_value}")
    void delCrawlConfig(CrawlerConfig crawlerConfig);
}
