package com.seaboxdata.cqny.record.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.BaseData;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface IBaseDataDao {

    static final String selector = "select data_id as dataId,parent_id as parentId,data_name as dataName,data_val as dataVal,data_type as dataType,create_time as createTime,create_user as createUser from report_base_data ";

    @Select(selector)
    Page<BaseData> pageingBaseDatas(@Param("currPage") int currPage, @Param("pageSize") int pageSize);

    @Select(selector)
    List<BaseData> listAllBaseDatas();

    @Insert("insert into report_base_data (parent_id,data_name ,data_val,data_type ,create_time ,create_user) values (#{parentId},#{dataName},#{dataVal},#{dataType},#{createTime},#{createUser})")
    @Options(useGeneratedKeys = true, keyProperty = "dataId", keyColumn = "data_id")
    void createBaseDatas(BaseData baseData);

}
