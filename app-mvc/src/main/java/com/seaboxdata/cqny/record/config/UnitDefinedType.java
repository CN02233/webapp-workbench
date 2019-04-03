package com.seaboxdata.cqny.record.config;

public enum UnitDefinedType {

    ONEDIMSTATIC(1),//一维静态
    ONEDIMDYNAMIC(2),//一维动态
    MANYDIMSTATIC(3),//多维静态
    MANYDIMTREE(4);//树状动态

    private Integer value;
    UnitDefinedType(Integer value){
        this.value  = value;
    }

    public Integer getValue(){
        return this.value;
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
