package com.crawler.webapp.crawlerpage.service.imp;

import com.AbstractTestService;
import com.crawler.webapp.crawlerpage.bean.JobPage;
import com.crawler.webapp.crawlerpage.service.CrawlerPageViewService;
import com.github.pagehelper.Page;
import org.junit.Test;

import javax.annotation.Resource;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by SongCQ on 2017/7/28.
 */
public class CrawlerPageViewServiceImpTest extends AbstractTestService{

    @Resource
    private CrawlerPageViewService crawlerPageViewService;

    @Test
    public void listCrawlerPage() throws Exception {
        JobPage jobPage = new JobPage();

        jobPage.setPage_url("1");
        jobPage.setJob_name("新浪");

        jobPage.setDownload_time_start("2017-07-01");
        jobPage.setDownload_time_end("2017-07-30");
        Page<JobPage> resl = crawlerPageViewService.listCrawlerPage(1, 10, jobPage);
        System.out.print("");
    }

}