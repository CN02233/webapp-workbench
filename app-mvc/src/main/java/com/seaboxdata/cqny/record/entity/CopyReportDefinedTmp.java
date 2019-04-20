package com.seaboxdata.cqny.record.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CopyReportDefinedTmp {

    private Integer fromUnit;

    private Integer toUnit;

    private Map<Integer,Integer> fromAndToColumId = new HashMap<>();

    private Map<Integer,Integer> fromAndToDimId = new HashMap<>();

    private ArrayList<Integer> fromColumId = new ArrayList<>();

    private ArrayList<Integer> toColumId = new ArrayList<>();

    private ArrayList<Boolean> isFomular = new ArrayList<>();

    public Integer getFromUnit() {
        return fromUnit;
    }

    public void setFromUnit(Integer fromUnit) {
        this.fromUnit = fromUnit;
    }

    public Integer getToUnit() {
        return toUnit;
    }

    public void setToUnit(Integer toUnit) {
        this.toUnit = toUnit;
    }

    public ArrayList<Integer> getFromColumId() {
        return fromColumId;
    }

    public ArrayList<Integer> getToColumId() {
        return toColumId;
    }

    public ArrayList<Boolean> getIsFomular() {
        return isFomular;
    }

    public Map<Integer, Integer> getFromAndToColumId() {
        return fromAndToColumId;
    }

    public Map<Integer, Integer> getFromAndToDimId() {
        return fromAndToDimId;
    }

    public void setFromAndToDimId(Map<Integer, Integer> fromAndToDimId) {
        this.fromAndToDimId = fromAndToDimId;
    }
}
