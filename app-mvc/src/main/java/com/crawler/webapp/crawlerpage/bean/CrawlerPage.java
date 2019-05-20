package com.crawler.webapp.crawlerpage.bean;

/**
 * Created by SongCQ on 2017/7/25.
 */
public class CrawlerPage {

    private int page_id;

    private int job_id;

    private int user_id;

    private String page_name;

    private int page_type;

    private int data_format;

    private int is_multi_page;

    private String paginate_element;

    private String load_indicator;

    private int page_interval;

    private int max_page_num;

    private int save_page_source;

    private String data_file;

    public int getPage_id() {
        return page_id;
    }

    public void setPage_id(int page_id) {
        this.page_id = page_id;
    }

    public int getJob_id() {
        return job_id;
    }

    public void setJob_id(int job_id) {
        this.job_id = job_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getPage_name() {
        return page_name;
    }

    public void setPage_name(String page_name) {
        this.page_name = page_name;
    }

    public int getPage_type() {
        return page_type;
    }

    public void setPage_type(int page_type) {
        this.page_type = page_type;
    }

    public int getData_format() {
        return data_format;
    }

    public void setData_format(int data_format) {
        this.data_format = data_format;
    }

    public int getIs_multi_page() {
        return is_multi_page;
    }

    public void setIs_multi_page(int is_multi_page) {
        this.is_multi_page = is_multi_page;
    }

    public String getPaginate_element() {
        return paginate_element;
    }

    public void setPaginate_element(String paginate_element) {
        this.paginate_element = paginate_element;
    }

    public String getLoad_indicator() {
        return load_indicator;
    }

    public void setLoad_indicator(String load_indicator) {
        this.load_indicator = load_indicator;
    }

    public int getPage_interval() {
        return page_interval;
    }

    public void setPage_interval(int page_interval) {
        this.page_interval = page_interval;
    }

    public int getMax_page_num() {
        return max_page_num;
    }

    public void setMax_page_num(int max_page_num) {
        this.max_page_num = max_page_num;
    }

    public int getSave_page_source() {
        return save_page_source;
    }

    public void setSave_page_source(int save_page_source) {
        this.save_page_source = save_page_source;
    }

    public String getData_file() {
        return data_file;
    }

    public void setData_file(String data_file) {
        this.data_file = data_file;
    }
}
