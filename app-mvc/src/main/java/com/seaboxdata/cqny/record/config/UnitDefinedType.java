package com.seaboxdata.cqny.record.config;

public enum UnitDefinedType {

    ONEDIMSTATIC(1),
    ONEDIMDYNAMIC(2),
    MANYDIMSTATIC(3),
    MANYDIMTREE(4);

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
