package com.seaboxdata.cqny.record.controller;


import com.seaboxdata.cqny.record.entity.BaseData;
import com.seaboxdata.cqny.record.service.BaseDataService;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("datas")
public class BaseDataController {

    @Autowired
    private BaseDataService baseDataService;

    @RequestMapping("listAllBaseDatas")
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public JsonResult listAllBaseDatas(){
        List<BaseData> allBaseDatas = baseDataService.listAllBaseDatas();
        JsonResult resultJson = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "获取成功", null, allBaseDatas);
        return resultJson;
    }
}
