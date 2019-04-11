package com.seaboxdata.cqny.record.entity;

/**
 * 0：正常
 * 1：审批中
 * 2：复核中
 * 3：锁定
 * 4：失效
 * 5：报表发布
 * 6：待上传签名
 * 7：过期
 */
public enum ReportStatus {

    NORMAL(0,"正常"),
    SUBMIT(1,"审批中"),
    REVIEW(2,"复核中"),
    LOCK(3,"锁定"),
    REMOVE(4,"失效"),
    APPROVE(5,"报表发布"),
    UP_SIGIN(6,"待上传签名"),
    TOO_EARLY(7,"未到填写日期"),
    OVER_TIME(8,"过期");

    private Integer value;
    private String comment;

    private ReportStatus(int value,String comment){
        this.comment=comment;
        this.value=value;
    }

    private ReportStatus(int value){
        this.value = value;
    }
    public String toString(){
        return this.value.toString();

    }

    public static void main(String[] args) {
        String s=ReportStatus.UP_SIGIN.getComment();
        System.out.println(ReportStatus.UP_SIGIN);
    }
    private String getComment() {
        return this.comment;
    }
}
