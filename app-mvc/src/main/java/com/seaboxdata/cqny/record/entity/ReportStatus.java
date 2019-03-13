package com.seaboxdata.cqny.record.entity;

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

    NORMAL(0),
    SUBMIT(1),
    REVIEW(2),
    LOCK(3),
    REMOVE(4),
    APPROVE(5),
    UP_SIGIN(6);

    private Integer value;

    private ReportStatus(int value){
        this.value = value;
    }
    public String toString(){
        return this.value.toString();
    }
}
