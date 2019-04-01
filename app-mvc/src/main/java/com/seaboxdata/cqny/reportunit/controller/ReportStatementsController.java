package com.seaboxdata.cqny.reportunit.controller;

import com.seaboxdata.cqny.reportunit.entity.StatementsEntity;
import com.seaboxdata.cqny.reportunit.service.ReportStatementsService;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import com.webapp.support.page.PageResult;
import com.webapp.support.session.SessionSupport;
import com.workbench.auth.user.entity.User;
import com.workbench.spring.aop.annotation.JsonpCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 报送报表的新增 修改 删除 查询
 */
@Controller
@RequestMapping("reportStatements")
public class ReportStatementsController {

    @Autowired
    private ReportStatementsService reportStatementsService;

    /**
     * 列表查询展示
     * @param currPage
     * @param pageSize
     * @return
     */
    @RequestMapping("listReportStatements")
    @ResponseBody
    @JsonpCallback
    @CrossOrigin(allowCredentials="true")
    public String listReportStatements(int currPage, int pageSize){
        PageResult originList = reportStatementsService.listReportStatements(currPage, pageSize);
        String jsonpResponse = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS, "获取成功", null, originList);
        return jsonpResponse;
    }

    /**
     * 新增/修改报送报表
     * @param reportStatements
     * @return
     */
    @RequestMapping("addReportStatements")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult addReportStatements(@RequestBody StatementsEntity reportStatements){
        User user = SessionSupport.checkoutUserFromSession();
        reportStatements.setCreate_user(user.getUser_id());
        reportStatementsService.addReportStatements(reportStatements);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "保存成功", null,null);
        return jsonResult;
    }


    /**
     * 删除报送报表
     * @param statementsId
     * @return
     */
    @RequestMapping("delById")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult deleteById( String statementsId){
        reportStatementsService.deleteById(statementsId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "删除成功", null,null);
        return jsonResult;
    }

    /**
     * 报送监管列表查询展示
     * 通过行政用户的ID->行政机构->报送机构->属于报送机构的所有报送报表
     * @param currPage
     * @param pageSize
     * @return
     */
    @RequestMapping("listReportStatementsByUser")
    @ResponseBody
    @JsonpCallback
    @CrossOrigin(allowCredentials="true")
    public String listReportStatementsByUser(int currPage, int pageSize){
        User user = SessionSupport.checkoutUserFromSession();
        PageResult originList = reportStatementsService.listReportStatementsByUser(currPage, pageSize,user.getUser_id());
        String jsonpResponse = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS, "获取成功", null, originList);
        return jsonpResponse;
    }

}
