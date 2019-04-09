package com.seaboxdata.cqny.origin.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.origin.entity.Submitauthority;
import com.seaboxdata.cqny.origin.tree.EntityTree;
import com.seaboxdata.cqny.record.entity.Origin;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ISubmitauthorityDao {

    @Select("SELECT\n" +
            "\torigin_id id,\n" +
            "\torigin_name label,\n" +
            "\tparent_origin_id parentId\n" +
            "\n" +
            "FROM\n" +
            "\tsys_origin where origin_status!=3 ")
    List<EntityTree> listAllSubmitauthority();

    @Insert("INSERT INTO sys_origin (\n" +
            "\torigin_name,\n" +
            "\tparent_origin_id,\n" +
            "\torigin_status,\n" +
            "\tcreate_date,\n" +
            "\tcreate_user\n" +
            ")\n" +
            "VALUES\n" +
            "\t(\n" +
            "\t#{ origin_name },#{ parent_origin_id },#{ origin_status },sysdate(),#{ create_user }\n" +
            "\t)")
    void addSubmitauthority(Submitauthority submitauthority);

    @Update("<script>update sys_origin <set>" +
            " origin_status = 3" +
            "</set>" +
            "where origin_id in"+
            "<foreach item='item' index='index' collection='listId' open='(' separator=',' close=')'> " +
            " #{item} " +
            "</foreach></script>")
    void deleteByListId(@Param("listId")List listId);

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
            +"<if test='create_user!=null'>"
            +"create_user=#{create_user} ,"
            +"</if>"
            +"create_date=sysdate() "
            +"</set>where origin_id = #{origin_id}</script>")
    void updateSubmitauthority(Submitauthority submitauthority);

    @Select("select origin_id,origin_name,parent_origin_id,origin_status,create_date,create_user from sys_origin where  origin_status!=3")
    Page<Submitauthority> listSubmitauthority(@Param("currPage") int currPage, @Param("pageSize") int pageSize);

    @Select("select * from sys_origin where origin_id = #{origin_id} and origin_status!=3")
    @Results(value={
            @Result(property = "origin_id",column = "origin_id"),
            @Result(property = "childrens",column = "origin_id" ,javaType= List.class, many=@Many(select="getSonOrigins"))})
    Map<String,Object> getOriginById(String origin_id);

    @Select("select * from sys_origin where parent_origin_id=#{parent_id} and origin_status!=3")
    @Results(value={
            @Result(property = "origin_id",column = "origin_id"),
            @Result(property = "childrens",column = "origin_id" ,javaType= List.class, many=@Many(select="getSonOrigins"))})
    List<Map<String,Object>> getSonOrigins(Integer parent_id);

    @Select("select distinct so.* from sys_origin so ,user_origin_assign uoa where so.origin_id = uoa.origin_id " +
            "and uoa.user_id = #{userId} and origin_status!=3")
    Origin getOriginByUserId(Integer userId);
}
