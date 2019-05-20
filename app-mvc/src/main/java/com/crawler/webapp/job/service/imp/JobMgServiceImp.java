package com.crawler.webapp.job.service.imp;

import com.crawler.webapp.job.bean.CrawlerServers;
import com.crawler.webapp.job.bean.JobInfoBean;
import com.crawler.webapp.job.bean.JobStatus;
import com.crawler.webapp.job.dao.IJobMgDao;
import com.crawler.webapp.job.service.JobMgService;
import com.crawler.webapp.proxyserver.bean.ProxyServer;
import com.github.pagehelper.Page;
import com.webapp.support.httpClient.HttpSendMessage;
import com.webapp.support.session.SessionSupport;
import com.workbench.auth.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by SongCQ on 2017/7/31.
 */
@Service("jobMgService")
public class JobMgServiceImp implements JobMgService {

    private Logger logger = LoggerFactory.getLogger(JobMgServiceImp.class);

    @Autowired
    private IJobMgDao iJobMgDao;

    @Autowired
    private CrawlerServers crawlerServers;

    @Override
    public Page<JobInfoBean> pagingCrawlList(int currPage,int pageSize, JobInfoBean jobInfoBean) {
        Page<JobInfoBean> resultPage = iJobMgDao.pagingCrawlList(currPage, pageSize, jobInfoBean);
        for(JobInfoBean pageData : resultPage){
            JobStatus jobStatus = pageData.getJobStatus();
            if(jobStatus!=null){}
            else pageData.setJobStatus(new JobStatus());
        }
        return resultPage;
    }

    @Override
    public List<JobInfoBean> listAllJob(){
        return iJobMgDao.listAllJob();
    }

    @Override
    public Page<JobInfoBean> pagingListByHost(int currPage, int pageSize, Integer host_id,String host_name) {
        Page<JobInfoBean> resultPage = iJobMgDao.pagingListByHost(currPage, pageSize,host_id, host_name);
        for(JobInfoBean pageData : resultPage){
            JobStatus jobStatus = pageData.getJobStatus();
            if(jobStatus!=null){}
            else pageData.setJobStatus(new JobStatus());
        }
        return resultPage;
    }

    @Override
    public List<Map<String, Object>> crawlSrcType() {
        return iJobMgDao.crawlSrcType();
    }

    @Override
    public List<Map<String, Object>> dataStore() {
        return iJobMgDao.dataStore();
    }

    @Override
    public List<Map<String, Object>> jobScheduleList() {
        return iJobMgDao.jobScheduleList();
    }

    @Override
    public List<Map<String, Object>> jobHostList() {
        return iJobMgDao.jobHostList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveNewJob(JobInfoBean jobInfo, List<String> proxyServers) {
        SimpleDateFormat format = new SimpleDateFormat("ssSSS");
        StringBuilder builder = new StringBuilder();
        builder.append(format.format(Calendar.getInstance().getTime()));
        builder.append(new Random().nextInt(50));
        int job_id = new Integer(builder.toString());
        job_id = job_id<<(new Random().nextInt(5));

        if(jobInfo.getIs_valid()==null)
            jobInfo.setIs_valid(1);

        jobInfo.setJob_id(job_id);

        iJobMgDao.saveJobInfo(jobInfo);
        if(proxyServers!=null&&proxyServers.size()>0){}//do nothing}
        else
            proxyServers = jobInfo.getProxyServerList();
        if(proxyServers!=null){
            for(String proxyServerId:proxyServers){
                iJobMgDao.saveProxyServer(proxyServerId,job_id,jobInfo.getUser_id());

            }
        }
    }

    @Override
    public JobInfoBean getCrawlData(int job_id) {
        JobInfoBean resultData = iJobMgDao.getCrawlData(job_id);
        return resultData;
    }

    @Override
    public List<ProxyServer> listAllProxyServerByJob(int job_id) {
        List<ProxyServer> resultData = iJobMgDao.listAllProxyServerByJob(job_id);
        return resultData;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateJobInfo(JobInfoBean jobInfo, ArrayList<String> proxyServers) {
        iJobMgDao.updateJobInfo(jobInfo);
        iJobMgDao.deleteAllProxyServer(jobInfo.getJob_id());
        if(proxyServers!=null&&proxyServers.size()>0){}//do nothing}
        else
            proxyServers = jobInfo.getProxyServerList();
        if(proxyServers!=null){
            for(String proxyServerId:proxyServers){
                if(jobInfo.getUser_id()==null){
                    User user = SessionSupport.checkoutUserFromSession();
                    jobInfo.setUser_id(user.getUser_id());
                }
                iJobMgDao.saveProxyServer(proxyServerId,jobInfo.getJob_id(),jobInfo.getUser_id());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleJob(int job_id) {
        //删除采集页面定义表
        iJobMgDao.removePageLinksByJob(job_id);
        iJobMgDao.removeLocateRelationByJob(job_id);
        iJobMgDao.removePageFieldsByJob(job_id);
        iJobMgDao.deleteCrawlerPageByJob(job_id);

        //删除任务信息
        iJobMgDao.delJob(job_id);
        //删除任务与代理服务器对应关系
        iJobMgDao.deleteAllProxyServer(job_id);
    }


    @Override
    public String startJob(int job_id, int user_id){
        String crawlerServer = crawlerServers.getCrawlerServer();
        String resultMsg = callJob(crawlerServer, "start", job_id, user_id);
        return resultMsg;
    }
    @Override
    public String stopJob(int job_id, int user_id){
        String crawlerServer = crawlerServers.getCrawlerServer();
        String resultMsg = callJob(crawlerServer, "stop", job_id, user_id);
        return resultMsg;
    }
    @Override
    public String updateJob(int job_id, int user_id){
        String crawlerServer = crawlerServers.getCrawlerServer();
        String resultMsg = callJob(crawlerServer, "update", job_id, user_id);
        return resultMsg;
    }


    private String callJob(String url,String callType,int job_id, int user_id){
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("command",callType);
        paramMap.put("job_id",job_id);
        paramMap.put("user_id",user_id);
        String resultMsg = HttpSendMessage.postHttpRequest4Str(url, paramMap);
        logger.debug("send to crawler start msg,response value is --->{}<---",resultMsg);

        return resultMsg;
    }
}
