package com.crawler.webapp.crawlerpage.service.imp;

import com.crawler.webapp.crawlerpage.bean.CrawlerPage;
import com.crawler.webapp.crawlerpage.bean.PageField;
import com.crawler.webapp.crawlerpage.bean.PageFieldLocate;
import com.crawler.webapp.crawlerpage.bean.PageLink;
import com.crawler.webapp.crawlerpage.dao.CrawlerPageMgDao;
import com.crawler.webapp.crawlerpage.service.CrawlerPageMgService;
import com.github.pagehelper.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

/**
 * Created by SongCQ on 2017/7/25.
 */
@Service("crawlerPageMgService")
public class CrawlerPageMgServiceImp implements CrawlerPageMgService{

    private Logger logger = LoggerFactory.getLogger(CrawlerPageMgServiceImp.class);

    @Autowired
    private CrawlerPageMgDao crawlerPageMgDao;

    @Override
    public Page<CrawlerPage> listCrawlerPageByPaging(int currPage, int pageSize) {
        Page<CrawlerPage> resultList = crawlerPageMgDao.listCrawlerPageByPaging(currPage, pageSize);
        return resultList;
    }

    @Override
    public CrawlerPage craPageData(int page_id, int job_id, int user_id) {
        CrawlerPage crawlerPageData = crawlerPageMgDao.craPageData(page_id, job_id, user_id);
        return crawlerPageData;
    }

    @Override
    public List<PageLink> listPageLink(int page_id, int job_id, int user_id) {
        List<PageLink> result = crawlerPageMgDao.listPageLink(page_id, job_id, user_id);
        return result;
    }

    @Override
    public List<PageField> listPageField(int page_id, int job_id, int user_id) {
        List<PageField> resultList = crawlerPageMgDao.listPageField(page_id, job_id, user_id);
        return resultList;
    }

    @Override
    public void newSaveCrawlerPage(CrawlerPage crawlerPage) {
        Integer maxPageId = crawlerPageMgDao.getMaxPageId(
                crawlerPage.getJob_id(),
                crawlerPage.getUser_id());
        if(maxPageId==null)
            maxPageId = 0;
        maxPageId = (maxPageId|1) + 1;
        crawlerPage.setPage_id(maxPageId);

        crawlerPageMgDao.newSaveCrawlerPage(crawlerPage);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public void updateCrawlerPage(CrawlerPage crawlerPage){
        crawlerPageMgDao.updateCrawlerPage(crawlerPage);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public void deleteCrawlerPage(int page_id, int job_id, int user_id){
        removeAllPageLinks(page_id,job_id,user_id);
        removeAllPageFields(page_id, job_id, user_id);
        crawlerPageMgDao.deleteCrawlerPage(page_id,job_id,user_id);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public void newSavePageFields(List<PageField> pageFields) {

        logger.debug("pageFields list value is {}",pageFields);

        for(PageField pageField : pageFields){
            crawlerPageMgDao.savePageField(pageField);
            int fieldId = pageField.getField_id();
            int relationId = new Integer(new StringBuilder().append(pageField.getPage_id()).
                    append(pageField.getJob_id()).append(pageField.getUser_id()).append(fieldId).
                    toString());
            crawlerPageMgDao.savePageFiledLocateRelation(fieldId,pageField.getPage_id(),pageField.getJob_id(),pageField.getUser_id(),relationId);

            PageFieldLocate pageFieldLocate = pageField.getPageFieldLocate();
            if(pageFieldLocate!=null){
                pageFieldLocate.setField_locate_id(relationId);
                crawlerPageMgDao.savePageFieldLocate(pageFieldLocate);
            }
        }
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public void newSavePageLinks(List<PageLink> pageLinks) {
        for (PageLink pageLink:pageLinks){
            crawlerPageMgDao.savePageLink(pageLink);
        }
    }


    @Override
    @Transactional(rollbackFor=Exception.class)
    public void updatePageLinks(List<PageLink> pageLinks){
        pageLinks.get(0).getPage_id();
        pageLinks.get(0).getJob_id();
        pageLinks.get(0).getUser_id();
        this.removeAllPageLinks(pageLinks.get(0).getPage_id(),pageLinks.get(0).getJob_id(),pageLinks.get(0).getUser_id());
        this.newSavePageLinks(pageLinks);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public void updatePageFields(List<PageField> pageFields){
        if(pageFields!=null&&pageFields.size()>0){
            int pageId = pageFields.get(0).getPage_id();
            int jobId = pageFields.get(0).getJob_id();
            int userId = pageFields.get(0).getUser_id();
            removeAllPageFields(pageId,jobId,userId);
            newSavePageFields(pageFields);
        }
    }

    @Override
    public void removeAllPageLinks(int page_id, int job_id, int user_id) {
        crawlerPageMgDao.removePageLinks(0,page_id,job_id,user_id);
    }

    @Override
    public void removePageLink(int link_id,int page_id, int job_id, int user_id){
        crawlerPageMgDao.removePageLinks(link_id,page_id,job_id,user_id);

    }

    @Override
    public void removeAllPageFields(int page_id, int job_id, int user_id){
        crawlerPageMgDao.removeLocateRelation(page_id, job_id, user_id);

        crawlerPageMgDao.removePageFields(0,page_id,job_id,user_id);
    }

    @Override
    public void removePageFields(int field_id, int page_id, int job_id, int user_id){
        crawlerPageMgDao.removePageFields(field_id,page_id,job_id,user_id);
    }

    @Override
    public List<CrawlerPage> listCrawlerPage() {
        List<CrawlerPage> list = crawlerPageMgDao.listCrawlerPage();
        return list;
    }

}
