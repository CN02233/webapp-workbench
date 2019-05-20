package com.crawler.webapp.job.service;

import com.crawler.webapp.job.bean.CrawlerConfig;
import com.github.pagehelper.Page;

/**
 * Created by SongCQ on 2017/10/10.
 */
public interface JobConfigService {
    Page<CrawlerConfig> pagingCrawlConfigList(int currPage, int pageSize);

    void saveCrawlConfig(CrawlerConfig crawlerConfig);

    void delCrawlConfig(CrawlerConfig crawlerConfig);
}
