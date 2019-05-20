package com.crawler.webapp.crawlerpage.controller;

import com.crawler.webapp.crawlerpage.bean.CrawlerPage;
import com.crawler.webapp.crawlerpage.bean.PageField;
import com.crawler.webapp.crawlerpage.bean.PageLink;
import com.crawler.webapp.crawlerpage.service.CrawlerPageMgService;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;
import com.webapp.support.session.SessionSupport;
import com.workbench.auth.user.entity.User;
import com.workbench.spring.aop.annotation.JsonMsgParam;
import com.workbench.spring.aop.annotation.JsonpCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SongCQ on 2017/7/25.
 */

@Controller
@RequestMapping("crawler/pageMg")
public class CrawlerPageMgController {

    @Autowired
    private CrawlerPageMgService crawlerPageMgService;

    @RequestMapping("pagingCrawlerPage")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public String listCrawlerPageByPaging(int currPage, int pageSize){
        PageResult pageResult = PageResult.pageHelperList2PageResult(
                crawlerPageMgService.listCrawlerPageByPaging(currPage, pageSize));
        String result = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS,"获取成功",null,pageResult);
        return result;
    }

    @RequestMapping("listCrawlerPage")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public String listCrawlerPage(){
        List<CrawlerPage> dataResult = crawlerPageMgService.listCrawlerPage();
        String result = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS,"获取成功",null,dataResult);
        return result;
    }

    @RequestMapping("craPageData")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public String craPageData(int page_id, int job_id, int user_id){
        return  JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS,"获取成功",null,
                crawlerPageMgService.craPageData(page_id, job_id, user_id));

    }

    @RequestMapping("listPageLink")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public String listPageLink(int page_id, int job_id, int user_id) {

        return  JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS,"获取成功",null,
                crawlerPageMgService.listPageLink(page_id, job_id, user_id));
    }

    @RequestMapping("listPageField")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public String listPageField(int page_id, int job_id, int user_id) {
        return  JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS,"获取成功",null,
                crawlerPageMgService.listPageField(page_id, job_id, user_id));
    }

    /*
        新增
     */
    @RequestMapping("newSaveCrawlerPage")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public String newSaveCrawlerPage(CrawlerPage crawlerPage){
        User user = SessionSupport.checkoutUserFromSession();
        crawlerPage.setUser_id(user.getUser_id());

        CrawlerPage checkResult = crawlerPageMgService.craPageData(crawlerPage.getPage_id(), crawlerPage.getJob_id(), crawlerPage.getUser_id());
        if(checkResult!=null)
            return JsonSupport.makeJsonResultStr(JsonResult.RESULT.FAILD,"保存失败",
                    "当前用户下已新增过PAGE_ID:"+crawlerPage.getPage_id()+" JOB_ID:"+crawlerPage.getJob_id()+"的组合",null);
        crawlerPageMgService.newSaveCrawlerPage(crawlerPage);
        return JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS,"保存成功",null,null);
    }

    @RequestMapping("newSaveFields")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public String newSaveFields(@JsonMsgParam(jsonObjTypes = PageField.class,jsonName = "pageFields")
                                                ArrayList<PageField> pageFields){
        crawlerPageMgService.newSavePageFields(pageFields);
        return JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS,"保存成功",null,null);
    }

    @RequestMapping("newSaveLinks")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public String newSaveLinks(@JsonMsgParam(jsonObjTypes = PageLink.class,jsonName = "pageLinks")
                                           ArrayList<PageLink> pageLinks){
        crawlerPageMgService.newSavePageLinks(pageLinks);
        return JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS,"保存成功",null,null);
    }

    /*
        更新
     */
    @RequestMapping("updateCrawlerPage")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public String updateCrawlerPage(CrawlerPage crawlerPage){
        crawlerPageMgService.updateCrawlerPage(crawlerPage);
        return JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS,"保存成功",null,null);
    }

    @RequestMapping("updatePageLinks")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public String updatePageLinks(@JsonMsgParam(jsonObjTypes = PageLink.class)
                                                  ArrayList<PageLink> pageLinks){
        crawlerPageMgService.updatePageLinks(pageLinks);
        return JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS,"保存成功",null,null);
    }

    @RequestMapping("updatePageFields")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public String updatePageFields(@JsonMsgParam(jsonObjTypes = PageField.class)
                                                   ArrayList<PageField> pageFields){
        crawlerPageMgService.updatePageFields(pageFields);
        return JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS,"保存成功",null,null);
    }

    /*
        删除
     */
    @RequestMapping("deleteCrawlerPage")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public String deleteCrawlerPage(int page_id, int job_id, int user_id){
        crawlerPageMgService.deleteCrawlerPage(page_id, job_id, user_id);
        return JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS,"删除成功",null,null);
    }

    @RequestMapping("deletePageField")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public String deletePageField(int field_id,int page_id, int job_id, int user_id){
        crawlerPageMgService.removePageFields(field_id, page_id, job_id, user_id);
        return JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS,"删除成功",null,null);
    }

    @RequestMapping("deletePageLink")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public String deletePageLink(int link_id,int page_id, int job_id, int user_id){
        crawlerPageMgService.removePageLink(link_id, page_id, job_id, user_id);
        return JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS,"删除成功",null,null);
    }

}
