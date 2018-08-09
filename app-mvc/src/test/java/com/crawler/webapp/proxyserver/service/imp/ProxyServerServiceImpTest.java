package com.crawler.webapp.proxyserver.service.imp;

import com.AbstractTestService;
import com.crawler.webapp.proxyserver.bean.ProxyServer;
import com.crawler.webapp.proxyserver.service.ProxyServerService;
import org.junit.Test;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by SongCQ on 2017/8/2.
 */
public class ProxyServerServiceImpTest extends AbstractTestService{
    @Test
    public void delServer() throws Exception {
        proxyServerService.delServer(1530122);
    }

    @Test
    public void saveNewServer() throws Exception {
        ProxyServer proxyServer = new ProxyServer();
        proxyServer.setProxy_server_name("111");
        proxyServer.setProxy_server_ip("111");
        proxyServer.setProxy_user_password("111");
        proxyServer.setProxy_user_name("111");

        proxyServerService.saveNewServer(proxyServer);
    }

    @Test
    public void updateServer() throws Exception {
        ProxyServer proxyServer = new ProxyServer();
        proxyServer.setProxy_server_name("1112");
        proxyServer.setProxy_server_ip("1112");
        proxyServer.setProxy_user_password("1112");
        proxyServer.setProxy_user_name("1112");
        proxyServer.setProxy_server_id(1530122);

        proxyServerService.updateServer(proxyServer);
    }

    @Test
    public void proxyServers() throws Exception {
        proxyServerService.proxyServer(1);
    }

    @Test
    public void pagingProxyServers() throws Exception {
        proxyServerService.pagingProxyServers(1,10,null);
    }

    @Resource
    private ProxyServerService proxyServerService;

    @Test
    public void listProxyServers() throws Exception {
        List<ProxyServer> res = proxyServerService.listProxyServers();
        System.out.print("");
    }

}