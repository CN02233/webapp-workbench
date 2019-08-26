package com.seaboxdata.cqny.datamove.entity;

import com.webapp.support.json.JsonSupport;

public class DataMoveRowConfigGroup{
        private String tableColumName;
        private String dimId;
        private Integer rmbUnit;

        public String getTableColumName() {
            return tableColumName;
        }

        public void setTableColumName(String tableColumName) {
            this.tableColumName = tableColumName;
        }

        public String getDimId() {
            return dimId;
        }

        public void setDimId(String dimId) {
            this.dimId = dimId;
        }
        public String toString(){
            return JsonSupport.objectToJson(this);
        }

    public Integer getRmbUnit() {
        return rmbUnit;
    }

    public void setRmbUnit(Integer rmbUnit) {
        this.rmbUnit = rmbUnit;
    }
}
