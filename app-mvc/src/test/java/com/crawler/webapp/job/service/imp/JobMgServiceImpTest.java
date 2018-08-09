package com.crawler.webapp.job.service.imp;

import com.AbstractTestService;
import com.crawler.webapp.job.bean.JobInfoBean;
import com.crawler.webapp.job.service.JobMgService;
import com.github.pagehelper.Page;
import org.junit.Test;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by SongCQ on 2017/7/31.
 */
public class JobMgServiceImpTest extends AbstractTestService{
    @Test
    public void pagingListByHost() throws Exception {
        Page<JobInfoBean> aa = jobMgService.pagingListByHost(1, 10,null, "新浪");
        Page<JobInfoBean> bb = jobMgService.pagingListByHost(1, 10, 0,null);
        System.out.print("");
    }

    @Test
    public void deleJob() throws Exception {
        jobMgService.deleJob(2510633);
    }

    @Test
    public void saveNewJob() throws Exception {
        JobInfoBean jobInfoBean = new JobInfoBean();
        jobInfoBean.setJob_name("job_name");
        jobInfoBean.setMax_page_num(123);
        jobInfoBean.setStart_urls("setStart_urls");
        jobInfoBean.setPage_life_cycle(111);
        jobInfoBean.setMax_depth(222);

        List<String> serverList = new ArrayList();
//        serverList.add("1");
//        serverList.add("2");

        jobMgService.saveNewJob(jobInfoBean,null);
    }

    @Resource
    private JobMgService jobMgService;

    @Test
    public void pagingCrawlList() throws Exception {
        JobInfoBean jobInfoBean = new JobInfoBean();
//        jobInfoBean.setIs_valid(1);
//        jobInfoBean.setJob_name("新浪");

        Page<JobInfoBean> res = jobMgService.pagingCrawlList(1, 10, jobInfoBean);

        System.out.print("");
    }

    @Test
    public void startJob(){
        JobMgServiceImp jobMgServiceImp = new JobMgServiceImp();
        jobMgServiceImp.startJob(1,1);
    }

}