package com.crawler.webapp.job.controller;

import com.crawler.webapp.job.bean.CrawlerConfig;
import com.crawler.webapp.job.bean.JobInfoBean;
import com.crawler.webapp.job.service.JobConfigService;
import com.crawler.webapp.job.service.JobMgService;
import com.github.pagehelper.Page;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;
import com.workbench.spring.aop.annotation.JsonpCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by SongCQ on 2017/10/10.
 */
@Controller
@RequestMapping("crawler/jobConfig")
public class JobConfigController {
    private Logger logger = LoggerFactory.getLogger(JobMgController.class);

    @Autowired
    private JobConfigService jobConfigService;

    @RequestMapping("pagingList")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public String pagingCrawlConfigList(int currPage,int pageSize){
        Page<CrawlerConfig> crawListPage = jobConfigService.pagingCrawlConfigList(currPage, pageSize);
        PageResult pageResult = PageResult.pageHelperList2PageResult(crawListPage);
        String result = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS,"获取成功",null,pageResult);

        logger.debug("paging crawl list result :{}",result);
        return result;
    }

    @RequestMapping("saveNewConfig")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public String saveCrawlConfig(CrawlerConfig crawlerConfig){
        jobConfigService.saveCrawlConfig(crawlerConfig);
        String result = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS,"保存成功",null,null);
        return  result;
    }

    @RequestMapping("deleConfig")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public String delCrawlConfig(CrawlerConfig crawlerConfig){
        jobConfigService.delCrawlConfig(crawlerConfig);
        String result = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS,"保存成功",null,null);
        return  result;
    }

}
