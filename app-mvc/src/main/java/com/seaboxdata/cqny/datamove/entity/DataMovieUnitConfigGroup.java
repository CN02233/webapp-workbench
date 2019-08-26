package com.seaboxdata.cqny.datamove.entity;

import com.webapp.support.json.JsonSupport;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataMovieUnitConfigGroup{
        private String rid;
        private String columId;
        private Map<String,DataMoveRowConfigGroup> dataMoveRowConfigGroup = new LinkedHashMap<>();//key:C1 C2 C3 C4

        public String getRid() {
            return rid;
        }

        public void setRid(String rid) {
            this.rid = rid;
        }

        public String getColumId() {
            return columId;
        }

        public void setColumId(String columId) {
            this.columId = columId;
        }

        public String toString(){
            return JsonSupport.objectToJson(this);
        }

    public Map<String, DataMoveRowConfigGroup> getDataMoveRowConfigGroup() {
        return dataMoveRowConfigGroup;
    }

    public void setDataMoveRowConfigGroup(Map<String, DataMoveRowConfigGroup> dataMoveRowConfigGroup) {
        this.dataMoveRowConfigGroup = dataMoveRowConfigGroup;
    }
}
