package com.seaboxdata.nlp.client.controller;

import com.webapp.support.httpClient.HttpClientSupport;
import com.webapp.support.json.JsonSupport;
import com.webapp.support.jsonp.JsonResult;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;

/**
 * Created by SongCQ on 2018/9/29.
 */

@Controller
@RequestMapping()
@MultipartConfig
public class SemanticAnalysisClientController extends AbstractClientController{

    Logger logger = LoggerFactory.getLogger(SemanticAnalysisClientController.class);

    private final static String pythonHost = "http://10.10.10.41:8888";
//    private final static String pythonHost = "http://192.168.1.168:8888";

    private HttpClientSupport httpClientSupport = HttpClientSupport.getInstance(pythonHost);

    @RequestMapping(path = "/nlp/test",method = {RequestMethod.GET})
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public String nlpTestRequest() throws IOException {
        logger.debug("get is runnning......");
        return JsonSupport.makeJsonResultStr(JsonResult.RESULT.SUCCESS,"YYY",null,null);
    }

    @RequestMapping(path = "/nlp/**",method = {RequestMethod.GET,RequestMethod.POST,RequestMethod.DELETE,RequestMethod.PUT})
    @ResponseBody
    @CrossOrigin(allowCredentials="true")
    public String nlpRequest(HttpServletRequest request) throws IOException, URISyntaxException, ServletException {
        String response = clientRequest(request,true);
        JsonResult jsonpResult   = JsonSupport.makeJsonpResult(JsonResult.RESULT.SUCCESS, "YYY", null, JsonSupport.jsonToMap(response));
        return JsonSupport.objectToJsonWithoutFormatter(jsonpResult);
    }

    @RequestMapping("/nlpdownload/**")
    public void nlpDownload(HttpServletRequest request, HttpServletResponse response) throws IOException, URISyntaxException {
        String serviceUrl = checkServiceUrl(request).replace("nlpdownload","nlp");

        URIBuilder uriBuilder = new URIBuilder(new StringBuilder()
                .append(pythonHost)
                .append("/").append(serviceUrl).toString());

        CloseableHttpResponse serviceResponse = this.httpClientSupport.sendGet(uriBuilder.toString());
        InputStream serviceResponseStream = serviceResponse.getEntity().getContent();

        for(Header header : serviceResponse.getAllHeaders()){
            response.setHeader(header.getName(),header.getValue());
        }

        OutputStream outputStream = response.getOutputStream();

        byte[] buf = new byte[8192];
        int n;
        while ((n = serviceResponseStream.read(buf)) > 0) {
            outputStream.write(buf, 0, n);
        }
        outputStream.flush();
    }

    @Override
    public HttpClientSupport httpClientSupport() {
        return this.httpClientSupport;
    }
}
