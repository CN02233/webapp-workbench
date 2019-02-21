package com.seaboxdata.cqny.record.entity;

import com.webapp.support.json.JsonSupport;

public class ReportCell {

    private int row ;
    private int column;
    private String val;
    private boolean merged = false;
    private boolean input = false;

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public boolean isMerged() {
        return merged;
    }

    public void setMerged(boolean merged) {
        this.merged = merged;
    }

    public boolean isInput() {
        return input;
    }

    public void setInput(boolean input) {
        this.input = input;
    }

    public String toString(){
        return JsonSupport.objectToJson(this);
    }
}
