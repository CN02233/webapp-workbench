package com.webapp.support.httpClient;

import com.AbstractTestService;
import com.sun.org.apache.regexp.internal.RE;
import junit.framework.TestCase;
import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SongCQ on 2018/10/11.
 */
public class HttpClientSupportTest extends AbstractTestService {

    @Test
    public void testSendGet() throws Exception {
        HttpClientSupport httpClient = HttpClientSupport.getSingleInstance("http://192.168.1.150:8888");
        Map params = new HashMap<>();
        params.put("pagesize","10");
        params.put("page","1");
        httpClient.sendGet("/nlp/manage/dict");
    }

    @Test
    public void testSendPost() throws Exception {
//        /nlp/manage/dict
        HttpClientSupport httpClient = HttpClientSupport.getSingleInstance("http://192.168.1.150:8888");
        Map params = new HashMap<>();
//        params.put("term","1");
//        params.put("pos","2");
//        params.put("freq","3");

//        httpClient.sendPostByJson("/nlp/manage/dict",params);
        File sendFile = new File("D:\\File\\项目\\北京副中心\\语义分析v1.0.pptx");
        System.out.println(sendFile.isAbsolute());
        params.put("file",sendFile);
        httpClient.sendPostWithFile("/nlp/segment/input/upload",null,params);
    }

    @Test
    public void testSendDelete() throws Exception {
        HttpClientSupport httpClient = HttpClientSupport.getSingleInstance("http://192.168.1.150:8888");
        Map params = new HashMap<>();
        params.put("term","1");
        params.put("synonym","2");
//        httpClient.sendRequest("/nlp/manage/dict_synonym",params, RequestMethod.DELETE);

    }


    @Test
    public void testSendPut() throws Exception {
        HttpClientSupport httpClient = HttpClientSupport.getSingleInstance("http://192.168.1.150:8888");
        Map params = new HashMap<>();
        params.put("id","1");
        params.put("db","2");
        params.put("type","2");
        params.put("host","2");
        params.put("user","2");
        params.put("password","2");
//        httpClient.sendRequest("/nlp/manage/datasource",params, RequestMethod.PUT);
    }

}