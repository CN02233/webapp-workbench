package com.seaboxdata.nlp.client.controller;

import com.webapp.support.httpClient.HttpClientSupport;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

/**
 * Created by SongCQ on 2018/10/18.
 */

@Controller
@RequestMapping({"/jiebaset","/secadd","/ruledict","/rulelist"})
public class SenstiveClientController extends AbstractClientController{

    Logger logger = LoggerFactory.getLogger(SemanticAnalysisClientController.class);

    private final static String jieBasetPythonHost = "http://192.168.1.124:8001";
//    private final static String jieBasetPythonHost = "http://10.10.10.41:8001";

    private HttpClientSupport httpClientSupport = HttpClientSupport.getInstance(jieBasetPythonHost);

    @RequestMapping(path = "**",method = {RequestMethod.GET,RequestMethod.POST,RequestMethod.DELETE,RequestMethod.PUT})
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public String nlpRequest(HttpServletRequest request) throws IOException, URISyntaxException, ServletException {
        String response = clientRequest(request,false);
//        String response = null;

        return JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS,"YYY",null,JsonSupport.jsonToMap(response));
    }

    @Override
    public HttpClientSupport httpClientSupport() {
        return httpClientSupport;
    }
}
