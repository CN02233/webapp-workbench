package com.crawler.webapp.crawlerpage.service.imp;

import com.crawler.webapp.crawlerpage.bean.CrawlerPage;
import com.crawler.webapp.crawlerpage.bean.PageField;
import com.crawler.webapp.crawlerpage.bean.PageFieldLocate;
import com.crawler.webapp.crawlerpage.bean.PageLink;
import com.crawler.webapp.crawlerpage.dao.CrawlerPageMgDao;
import com.crawler.webapp.crawlerpage.service.CrawlerPageMgService;
import com.github.pagehelper.Page;
import com.crawler.webapp.util.tree.EntityTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by SongCQ on 2017/7/25.
 */
@Service("crawlerPageMgService")
public class CrawlerPageMgServiceImp implements CrawlerPageMgService{

    private Logger logger = LoggerFactory.getLogger(CrawlerPageMgServiceImp.class);

    @Autowired
    private CrawlerPageMgDao crawlerPageMgDao;

    @Override
    public Page<CrawlerPage> listCrawlerPageByPaging(int currPage, int pageSize, CrawlerPage bean) {
        Page<CrawlerPage> resultList = crawlerPageMgDao.listCrawlerPageByPaging(currPage, pageSize, bean);
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
        Integer maxId = crawlerPageMgDao.getMaxPageId(
                crawlerPage.getJob_id(),
                crawlerPage.getUser_id());
        if(maxId==null)
            maxId = 1;
        else
            maxId++;
        crawlerPage.setPage_id(maxId);

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
        PageField newField = pageFields.get(0);
        Integer maxId = crawlerPageMgDao.getMaxFieldId(newField.getPage_id(), newField.getJob_id(), newField.getUser_id());
        if(maxId==null)
            maxId = 1;
        else
            maxId++;

        for(PageField pageField : pageFields){
            pageField.setField_id(maxId++);
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
        PageLink newLink = pageLinks.get(0);
        Integer maxId = crawlerPageMgDao.getMaxLinkId(newLink.getPage_id(), newLink.getJob_id(), newLink.getUser_id());
        if(maxId==null)
            maxId = 1;
        else
            maxId++;
        for (PageLink pageLink:pageLinks){
            pageLink.setLink_id(maxId++);
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
        crawlerPageMgDao.removeLocates(page_id, job_id, user_id);
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

    @Override
    public PageField craFieldData(int field_id, int page_id, int job_id, int user_id) {
        PageField pageField = crawlerPageMgDao.craFieldData(field_id, page_id, job_id, user_id);
        return pageField;
    }

    @Override
    public PageLink craLinkData(int link_id, int page_id, int job_id, int user_id) {
        PageLink pageLink = crawlerPageMgDao.craLinkData(link_id, page_id, job_id, user_id);
        return pageLink;
    }

    @Override
    public List<EntityTree> treePageField(int page_id, int job_id, int user_id) {
        List<EntityTree> resultList = crawlerPageMgDao.treePageField(page_id, job_id, user_id);
        return resultList;
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public void saveAllPageFields(Map<String, List<PageField>> maps) {
        if(maps.containsKey("edit")){
            List<PageField> list = maps.get("edit");
            if(list.size()>0)
                this.updatePageFields(list);
        }
        if(maps.containsKey("add")){
            List<PageField> list = maps.get("add");
            if(list.size()>0)
                this.newSavePageFields(list);
        }
        /*if(maps.containsKey("del")){
            for(PageField mod3 : maps.get("del")){
                int field_id = mod3.getField_id();
                int job_id = mod3.getJob_id();
                int page_id = mod3.getPage_id();
                int user_id = mod3.getUser_id();
                if(field_id > 0 && job_id > 0 && page_id > 0 && user_id > 0)
                    this.removePageFields(field_id, page_id,job_id, user_id);
            }
        }*/
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public void saveAllPageLinks(Map<String, List<PageLink>> maps) {
        if(maps.containsKey("edit")){
            List<PageLink> list = maps.get("edit");
            if(list.size()>0)
                this.updatePageLinks(list);
        }
        if(maps.containsKey("add")){
            List<PageLink> list = maps.get("add");
            if(list.size()>0)
                this.newSavePageLinks(list);
        }
    }

}
