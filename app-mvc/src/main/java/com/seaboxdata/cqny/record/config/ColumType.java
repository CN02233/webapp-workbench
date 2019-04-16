package com.seaboxdata.cqny.record.config;

public enum ColumType {

    NUMBER(1),//数字
    STRING(2),//字符串
    DATE(3),//日期
    FORMULA(0),//公式
    FINALVALUE(4);//常量值

    private Integer value;
    ColumType(Integer value){
        this.value  = value;
    }

    public Integer value(){
        return this.value;
    }

    public boolean compareWith(Integer type){
        boolean compareResult = false;
        if(type!=null&&this.value.equals(type) ){
            return true;
        }
        return false;
    }

}
