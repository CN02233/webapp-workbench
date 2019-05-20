package com.crawler.webapp.job.service.imp;

import com.crawler.webapp.job.bean.CrawlerConfig;
import com.crawler.webapp.job.dao.IJobConfigDao;
import com.crawler.webapp.job.service.JobConfigService;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by SongCQ on 2017/10/10.
 */
@Service("jobConfigService")
public class JobConfigServiceImp implements JobConfigService {

    @Autowired
    private IJobConfigDao iJobConfigDao;

    @Override
    public Page<CrawlerConfig> pagingCrawlConfigList(int currPage, int pageSize) {
        Page<CrawlerConfig> result = iJobConfigDao.pagingCrawlConfigList(currPage, pageSize);
        return result;
    }

    @Override
    public void saveCrawlConfig(CrawlerConfig crawlerConfig) {
        iJobConfigDao.saveCrawlConfig(crawlerConfig);
    }

    @Override
    public void delCrawlConfig(CrawlerConfig crawlerConfig) {
        iJobConfigDao.delCrawlConfig(crawlerConfig);

    }
}
