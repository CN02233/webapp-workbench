package com.seaboxdata.cqny.record.config;

public enum ColumType {

    NUMBER(1),
    STRING(2),
    DATE(3),
    FORMULA(0);

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
