package com.seaboxdata.cqny.reportunit.entity;

public class StatementsEntity {

    private Integer statements_id;
    private String statements_name;
    private Integer origin_id;
    private String status;
    private String create_time;
    private int create_user;
    //辅助属性、前端显示name
    private String origin_name;
    private String user_name;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getOrigin_name() {
        return origin_name;
    }

    public void setOrigin_name(String origin_name) {
        this.origin_name = origin_name;
    }

    public Integer getStatements_id() {
        return statements_id;
    }

    public void setStatements_id(Integer statements_id) {
        this.statements_id = statements_id;
    }

    public String getStatements_name() {
        return statements_name;
    }

    public void setStatements_name(String statements_name) {
        this.statements_name = statements_name;
    }

    public Integer getOrigin_id() {
        return origin_id;
    }

    public void setOrigin_id(Integer origin_id) {
        this.origin_id = origin_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int getCreate_user() {
        return create_user;
    }

    public void setCreate_user(int create_user) {
        this.create_user = create_user;
    }
}
