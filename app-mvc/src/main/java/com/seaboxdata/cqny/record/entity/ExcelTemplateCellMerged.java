package com.seaboxdata.cqny.record.entity;

import com.webapp.support.json.JsonSupport;

public class ExcelTemplateCellMerged {
    private Integer id;
    private Integer sheet_id;
    private Integer row;
    private Integer col;
    private Integer rowspan;
    private Integer colspan;
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getCol() {
        return col;
    }

    public void setCol(Integer col) {
        this.col = col;
    }

    public Integer getRowspan() {
        return rowspan;
    }

    public void setRowspan(Integer rowspan) {
        this.rowspan = rowspan;
    }

    public Integer getColspan() {
        return colspan;
    }

    public void setColspan(Integer colspan) {
        this.colspan = colspan;
    }

    public Integer getSheet_id() {
        return sheet_id;
    }

    public void setSheet_id(Integer sheet_id) {
        this.sheet_id = sheet_id;
    }

    public String toString(){
        return JsonSupport.objectToJson(this);
    }
}
