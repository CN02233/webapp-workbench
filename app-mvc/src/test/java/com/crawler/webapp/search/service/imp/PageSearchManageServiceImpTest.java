package com.crawler.webapp.search.service.imp;

import com.AbstractTestService;
import com.crawler.webapp.search.bean.SearchBean;
import com.crawler.webapp.search.service.PageSearchManageService;
import com.webapp.support.httpClient.HttpSendMessage;
import com.webapp.support.json.JsonSupport;
import org.junit.Test;

import javax.annotation.Resource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by SongCQ on 2017/9/27.
 */
public class PageSearchManageServiceImpTest extends AbstractTestService {
    @Test
    public void listJobAndTypes() throws Exception {
        List<Map<String, Object>> res = pageSearchManageService.listJobAndTypes();
        System.out.println(res.toString());
    }

    @Resource
    private PageSearchManageService pageSearchManageService;

    @Test
    public void listJobCategory() throws Exception {
        pageSearchManageService.listJobCategory();
    }

    @Test
    public void doSearch() throws Exception {
        SearchBean searchBean = new SearchBean();
//        searchBean.setSearchContent("长沙");
        Map pagingMap = new HashMap();
        pagingMap.put("2","0");
        pagingMap.put("4","0");
        searchBean.setRows("8");
        searchBean.setPageId("1");
        searchBean.setUrl("finance.sina.com.cn");
        searchBean.setPagingMap(pagingMap);
        searchBean.setJobStartDate("2017-09-01");
        List<Object> jobs = new ArrayList<>();
//        jobs.add(2);
        jobs.add(4);
        searchBean.setJobIdList(jobs);
//        Map<String, Object> allRe = pageSearchManageService.doSearch(searchBean);

        System.out.println("done");

        SearchBean searchBean1 = new SearchBean();
        searchBean1.setVersion("1578854558940004352");
        searchBean1.setJobIdList(jobs);
        Map<String, Object> result = pageSearchManageService.doSearch(searchBean1);
        System.out.println("done1");

    }

}