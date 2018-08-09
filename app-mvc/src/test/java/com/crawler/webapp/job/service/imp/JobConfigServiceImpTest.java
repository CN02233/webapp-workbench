package com.crawler.webapp.job.service.imp;

import com.AbstractTestService;
import com.crawler.webapp.job.bean.CrawlerConfig;
import com.crawler.webapp.job.service.JobConfigService;
import com.workbench.auth.group.service.GroupService;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created by SongCQ on 2017/10/10.
 */
public class JobConfigServiceImpTest extends AbstractTestService {
    @Test
    public void saveCrawlConfig() throws Exception {
        CrawlerConfig crawlConfig = new CrawlerConfig();
        crawlConfig.setJob_id(1);
        crawlConfig.setUser_id(1);
        crawlConfig.setParam_name("11");
        crawlConfig.setParam_value("22");
        jobConfigService.saveCrawlConfig(crawlConfig);
    }

    @Test
    public void delCrawlConfig() throws Exception {
        CrawlerConfig crawlConfig = new CrawlerConfig();
        crawlConfig.setJob_id(1);
        crawlConfig.setUser_id(1);
        crawlConfig.setParam_name("11");
        crawlConfig.setParam_value("22");
        jobConfigService.delCrawlConfig(crawlConfig);
    }

    @Resource
    private JobConfigService jobConfigService;

//    @Resource
//    private GroupService groupService;

    @Test
    public void pagingCrawlConfigList() {
        jobConfigService.pagingCrawlConfigList(1,10);
    }

}