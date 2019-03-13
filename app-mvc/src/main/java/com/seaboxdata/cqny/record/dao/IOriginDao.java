package com.seaboxdata.cqny.record.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.Origin;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

public interface IOriginDao {

    @Select("select origin_id,origin_name,parent_origin_id,origin_status,create_date,create_user from sys_origin")
    List<Origin> listAllOrigin();

    @Select("select origin_id,origin_name,parent_origin_id,origin_status,create_date,create_user from sys_origin")
    Page<Origin> listOrigin(@Param("currPage") int currPage, @Param("pageSize") int pageSize);

    @Insert("insert into sys_origin (origin_name,parent_origin_id,origin_status,create_date,create_user) values " +
            "(#{origin_name},#{parent_origin_id},#{origin_status},#{create_date},#{create_user})")
    @Options(useGeneratedKeys = true, keyProperty = "origin_id", keyColumn = "origin_id")
    void createOrigin(Origin origin);

    @Select("select * from sys_origin where origin_id = #{origin_id}")
    @Results(value={
            @Result(property = "origin_id",column = "origin_id"),
            @Result(property = "childrens",column = "origin_id" ,javaType= List.class, many=@Many(select="getSonOrigins"))})
    Map<String,Object> getOriginById(Integer origin_id);

    @Select("select * from sys_origin where parent_origin_id=#{parent_id}")
    @Results(value={
            @Result(property = "origin_id",column = "origin_id"),
            @Result(property = "childrens",column = "origin_id" ,javaType= List.class, many=@Many(select="getSonOrigins"))})
    List<Map<String,Object>> getSonOrigins(Integer parent_id);

    @Insert("insert into origin_template_assign (origin_id,template_id) values (#{originId},#{templateId})")
    void saveOriginTemplate(@Param("originId") Integer originId,@Param("templateId") Integer templateId);

    @Delete("delete from user_origin_assign where user_id = #{userId}")
    void removeUserOrigin(@Param("userId") Integer userId);

    @Insert("insert into user_origin_assign (origin_id,user_id) values (#{originId},#{userId})")
    void userOriginSave(@Param("originId") Integer originId,@Param("userId") Integer userId);

    @Select("select distinct so.* from sys_origin so ,user_origin_assign uoa where so.origin_id = uoa.origin_id " +
            "and uoa.user_id = #{userId}")
    Origin getOriginByUserId(Integer userId);

}
