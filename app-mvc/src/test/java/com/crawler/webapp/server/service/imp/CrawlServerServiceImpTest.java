package com.crawler.webapp.server.service.imp;

import com.AbstractTestService;
import com.crawler.webapp.server.bean.CrawlServer;
import com.crawler.webapp.server.service.CrawlServerService;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created by SongCQ on 2017/8/4.
 */
public class CrawlServerServiceImpTest extends AbstractTestService{

    @Resource
    private CrawlServerService cralServerService;

    @Test
    public void pagingServer() throws Exception {

    }

    @Test
    public void saveNewServer() throws Exception {
        CrawlServer crawlServer = new CrawlServer();
        crawlServer.setHost_ip("192.192.192.192");
        crawlServer.setHost_name("192.192.192.192");
        crawlServer.setHost_status(null);
        crawlServer.setUser_group_id(null);

        cralServerService.saveNewServer(crawlServer);
    }

}