package com.crawler.webapp.job.service.imp;

import com.AbstractTestService;
import com.crawler.webapp.job.service.JobStatusService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by SongCQ on 2017/8/3.
 */
public class JobStatusServiceImpTest extends AbstractTestService{

    @Autowired
    private JobStatusService jobStatusService;

    @Test
    public void pagingCrawlStatusList() throws Exception {
        jobStatusService.pagingCrawlStatusList(1,10,null);
    }

}