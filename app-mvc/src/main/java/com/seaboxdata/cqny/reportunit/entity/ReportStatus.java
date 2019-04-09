package com.seaboxdata.cqny.reportunit.entity;

/**
 * 0：正常
 * 1：审批中
 * 2：复核中
 * 3：锁定
 * 4：失效
 * 5：报表发布
 * 6：待上传签名
 */
public enum ReportStatus {

    NORMAL(0,"正常"),
    SUBMIT(1,"审批中"),
    REVIEW(2,"复核中"),
    LOCK(3,"锁定"),
    REMOVE(4,"失效"),
    APPROVE(5,"报表发布"),
    UP_SIGN(6,"待上传签名");

    private Integer value;
    private String comment;

    private ReportStatus(int value, String comment){
        this.comment=comment;
        this.value=value;
    }

    public String toString(){
        return this.value.toString();
    }

    public String getComment() {
        return this.comment;
    }
    public Integer getValue() {
        return this.value;
    }
}
