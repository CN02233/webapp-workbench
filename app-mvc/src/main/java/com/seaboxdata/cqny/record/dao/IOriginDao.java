package com.seaboxdata.cqny.record.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.origin.entity.Submitauthority;
import com.seaboxdata.cqny.record.entity.Origin;
import com.workbench.auth.user.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

public interface IOriginDao {

    @Select("select origin_id,origin_name,parent_origin_id,origin_status,create_date,create_user,origin_type from sys_origin")
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

    @Select("select * from sys_origin where origin_name like concat('%',#{searchOriginName},'%')")
    List<Origin> getOriginByName(String searchOriginName);

    @Select("select u.* from user u ,user_origin_assign uoa where u.user_id = uoa.user_id and uoa.origin_id = #{originId}")
    List<User> getUsersByOrigin(Integer originId);

    @Update("<script>update sys_origin <set>"
            +"<if test='origin_name!=null'>"
            +"origin_name=#{origin_name} ,"
            +"</if>"
            +"<if test='parent_origin_id!=null'>"
            +"parent_origin_id=#{parent_origin_id} ,"
            +"</if>"
            +"<if test='origin_status!=null'>"
            +"origin_status=#{origin_status} ,"
            +"</if>"
            +"<if test='origin_type!=null'>"
            +"origin_type=#{origin_type} ,"
            +"</if>"
            +"<if test='create_user!=null'>"
            +"create_user=#{create_user} ,"
            +"</if>"
            +"create_date=sysdate() "
            +"</set>where origin_id = #{origin_id}</script>")
    void updateOrigin(Origin origin);
}
