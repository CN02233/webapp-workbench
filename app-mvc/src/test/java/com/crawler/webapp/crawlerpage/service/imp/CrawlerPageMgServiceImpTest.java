package com.crawler.webapp.crawlerpage.service.imp;

import com.AbstractTestService;
import com.crawler.webapp.crawlerpage.bean.CrawlerPage;
import com.crawler.webapp.crawlerpage.bean.PageField;
import com.crawler.webapp.crawlerpage.bean.PageFieldLocate;
import com.crawler.webapp.crawlerpage.bean.PageLink;
import com.crawler.webapp.crawlerpage.service.CrawlerPageMgService;
import com.github.pagehelper.Page;
import org.junit.Test;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by SongCQ on 2017/7/25.
 */
public class CrawlerPageMgServiceImpTest extends AbstractTestService {
    @Test
    public void updateCrawlerPage() throws Exception {
        CrawlerPage crawlerPage = new CrawlerPage();
        crawlerPage.setUser_id(1);
        crawlerPage.setJob_id(1);
        crawlerPage.setPage_id(0);
        crawlerPage.setData_file("data file");
        crawlerPage.setData_format(12);
        crawlerPage.setIs_multi_page(12);
        crawlerPage.setLoad_indicator("loda in");
        crawlerPage.setMax_page_num(12);
        crawlerPage.setPage_interval(12);
        crawlerPage.setPage_name("page name");
        crawlerPage.setPage_type(12);
        crawlerPage.setPaginate_element("page element");
        crawlerPage.setSave_page_source(12);

        crawlerPageMgService.updateCrawlerPage(crawlerPage);
    }

    @Test
    public void updatePageLinks() throws Exception {
        List<PageLink> pagelinks = new ArrayList<>();
        for(int i=0;i<4;i++){
            PageLink pagelink = new PageLink();
            pagelink.setUser_id(1);
            pagelink.setJob_id(1);
            pagelink.setPage_id(1);
            pagelink.setLink_locate_pattern("//div[@id=\"d_list\"]/ul/li/span[@class=\"c_tit\"]/a");
            pagelink.setNext_page_id(2);
            pagelinks.add(pagelink);
        }
        crawlerPageMgService.updatePageLinks(pagelinks);
    }

    @Test
    public void newSavePageFields() throws Exception {
        List<PageField> pageFields = new ArrayList<>();

        for(int i=0;i<10;i++){
            PageField pageField = new PageField();
            pageField.setPage_id(1);
            pageField.setJob_id(1);
            pageField.setUser_id(1);
            pageField.setField_name("t:"+i);
            pageField.setField_datatype(0);
            pageField.setParent_field_id(0);
            pageField.setCombine_field_value(1);

            PageFieldLocate pageFieldLocate = new PageFieldLocate();
            pageFieldLocate.setField_locate_pattern("//div[@class=\"hanml\"]/div[@class=\"conMidtab\"][1]/div[@class=\"conMidtab3\"][${FIELD_LEVEL_1_INDEX}]/table/tr[1]/td[@class=\"rowsPan\"]");
            pageFieldLocate.setField_ext_pattern("self-text");

            pageField.setPageFieldLocate(pageFieldLocate);
            pageFields.add(pageField);
        }

        crawlerPageMgService.newSavePageFields(pageFields);

    }

    @Test
    public void newSavePageLinks() throws Exception {

        List<PageLink> pagelinks = new ArrayList<>();
        for(int i=0;i<3;i++){
            PageLink pagelink = new PageLink();
            pagelink.setUser_id(1);
            pagelink.setJob_id(1);
            pagelink.setPage_id(1);
            pagelink.setLink_locate_pattern("//div[@id=\"d_list\"]/ul/li/span[@class=\"c_tit\"]/a");
            pagelink.setNext_page_id(2);
            pagelinks.add(pagelink);
        }
        crawlerPageMgService.newSavePageLinks(pagelinks);


    }

    @Test
    public void newSaveCrawlerPage() throws Exception {
        CrawlerPage crawlerPage = new CrawlerPage();
        crawlerPage.setUser_id(2);
        crawlerPage.setJob_id(1);
        crawlerPageMgService.newSaveCrawlerPage(crawlerPage);
    }

    @Test
    public void listPageField() throws Exception {
        List<PageField> result = crawlerPageMgService.listPageField(1, 2, 1);
        System.out.println("");
    }

    @Test
    public void listPageLink() throws Exception {
        List<PageLink> resultList = crawlerPageMgService.listPageLink(1, 1, 1);
        System.out.println("");
    }

    @Test
    public void craPageData() throws Exception {
        CrawlerPage result = crawlerPageMgService.craPageData(1, 1, 1);
        System.out.println("");

    }

    @Resource
    private CrawlerPageMgService crawlerPageMgService;

    @Test
    public void listCrawlerPageByPaging() throws Exception {
        Page<CrawlerPage> result = crawlerPageMgService.listCrawlerPageByPaging(1, 10);
        System.out.println("");
    }

}