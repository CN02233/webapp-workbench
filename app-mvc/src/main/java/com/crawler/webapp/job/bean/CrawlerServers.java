package com.crawler.webapp.job.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by SongCQ on 2017/8/29.
 */
public class CrawlerServers {

    private List<String> serverAddresList;

    private String solrAddres;

    private AtomicInteger automicInteger = new AtomicInteger(0);

    public List<String> getServerAddresList() {
        return serverAddresList;
    }

    public void setServerAddresList(List<String> serverAddresList) {
        this.serverAddresList = serverAddresList;
    }

    public String getCrawlerServer(){
        int nowVal = automicInteger.getAndIncrement();
        System.out.println("nowVal ......."+nowVal);
        if(nowVal==serverAddresList.size()){
            automicInteger.set(0);
            return serverAddresList.get(0);
        }else{
            return serverAddresList.get(nowVal);
        }
    }

    public String getSolrAddres() {
        return solrAddres;
    }

    public void setSolrAddres(String solrAddres) {
        this.solrAddres = solrAddres;
    }
}
