package com.crawler.webapp.crawlerpage.service;

import com.crawler.webapp.crawlerpage.bean.CrawlerPage;
import com.crawler.webapp.crawlerpage.bean.PageField;
import com.crawler.webapp.crawlerpage.bean.PageLink;
import com.github.pagehelper.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by SongCQ on 2017/7/25.
 */
public interface CrawlerPageMgService {
    Page<CrawlerPage> listCrawlerPageByPaging(int currPage, int pageSize);

    CrawlerPage craPageData(int page_id,int job_id,int user_id);

    List<PageLink> listPageLink(int page_id, int job_id, int user_id);

    List<PageField> listPageField(int page_id, int job_id, int user_id);

    void newSaveCrawlerPage(CrawlerPage crawlerPage);

    void updateCrawlerPage(CrawlerPage crawlerPage);

    void deleteCrawlerPage(int page_id, int job_id, int user_id);

    void newSavePageFields(List<PageField> pageFields);

    void newSavePageLinks(List<PageLink> pageLinks);

    void updatePageFields(List<PageField> pageFields);

    void removeAllPageLinks(int page_id, int job_id, int user_id);

    void updatePageLinks(List<PageLink> pageLinks);

    void removePageLink(int link_id,int page_id, int job_id, int user_id);

    void removeAllPageFields(int page_id, int job_id, int user_id);

    void removePageFields(int field_id, int page_id, int job_id, int user_id);

    List<CrawlerPage> listCrawlerPage();
}
