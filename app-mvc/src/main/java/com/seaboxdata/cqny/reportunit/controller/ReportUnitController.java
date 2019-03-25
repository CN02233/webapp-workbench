package com.seaboxdata.cqny.reportunit.controller;

import com.seaboxdata.cqny.reportunit.entity.UnitEntity;
import com.seaboxdata.cqny.reportunit.service.ReportUnitService;
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
 * 报送单元的新增 修改 删除 查询
 */
@Controller
@RequestMapping("reportUnit")
public class ReportUnitController {

    @Autowired
    private ReportUnitService originService;

    /**
     * 列表查询展示
     * @param currPage
     * @param pageSize
     * @return
     */
    @RequestMapping("listReportUnit")
    @ResponseBody
    @JsonpCallback
    @CrossOrigin(allowCredentials="true")
    public String listReportUnit(int currPage, int pageSize){
        PageResult originList = originService.listReportUnit(currPage, pageSize);
        String jsonpResponse = JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS, "获取成功", null, originList);
        return jsonpResponse;
    }

    /**
     * 新增/修改报送单元
     * @param reportUnit
     * @return
     */
    @RequestMapping("addReportUnit")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult addReportUnit(@RequestBody UnitEntity reportUnit){
        User user = SessionSupport.checkoutUserFromSession();
        reportUnit.setCreate_user(user.getUser_id());
        originService.addReportUnit(reportUnit);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "保存成功", null,null);
        return jsonResult;
    }


    /**
     * 删除报送单元
     * @param unitId
     * @return
     */
    @RequestMapping("delById")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult deleteById( String unitId){
        originService.deleteById(unitId);
        JsonResult jsonResult = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "删除成功", null,null);
        return jsonResult;
    }

}
