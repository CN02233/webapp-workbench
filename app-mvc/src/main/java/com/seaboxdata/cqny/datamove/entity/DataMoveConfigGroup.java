package com.seaboxdata.cqny.datamove.entity;

import com.webapp.support.json.JsonSupport;

import java.util.HashMap;
import java.util.Map;

public class DataMoveConfigGroup{
        private String tableName;
        private String unitId;
        private Map<String,DataMovieUnitConfigGroup> dataMovieUnitConfigGroup = new HashMap<>();//key：rid value:

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getUnitId() {
            return unitId;
        }

        public void setUnitId(String unitId) {
            this.unitId = unitId;
        }

        public Map<String, DataMovieUnitConfigGroup> getDataMovieUnitConfigGroup() {
            return dataMovieUnitConfigGroup;
        }

        public void setDataMovieUnitConfigGroup(Map<String, DataMovieUnitConfigGroup> dataMovieUnitConfigGroup) {
            this.dataMovieUnitConfigGroup = dataMovieUnitConfigGroup;
        }

        public String toString(){
            return JsonSupport.objectToJson(this);
        }
    }
