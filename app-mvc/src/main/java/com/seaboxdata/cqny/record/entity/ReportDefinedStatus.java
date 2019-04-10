package com.seaboxdata.cqny.record.entity;

/**
 * 0:正常
 * 1:失效
 * 2:锁定
 * 3:软删除
 * 4:已发布
 * 5:发布中
 */
public enum ReportDefinedStatus {
    NORMAL(0),
    FAIL(1),
    LOCK(2),
    DELETE(3),
    SUBMITING(5),
    SUBMIT(4);

    private Integer value;

    private ReportDefinedStatus(int value){
        this.value = value;
    }
    public String toString(){
        return this.value.toString();
    }
    public boolean compareWith(Integer status){
        boolean compareResult = false;
        if(status!=null&&this.value.equals(status) ){
            return true;
        }
        return false;
    }

    public int value(){
        return this.value;
    }
}
