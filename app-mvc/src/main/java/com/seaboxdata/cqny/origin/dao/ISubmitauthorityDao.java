package com.seaboxdata.cqny.origin.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.origin.entity.Submitauthority;
import com.seaboxdata.cqny.origin.test.EntityTree;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISubmitauthorityDao {

    @Select("SELECT\n" +
            "\torigin_id id,\n" +
            "\torigin_name name,\n" +
            "\tparent_origin_id parentId\n" +
            "\n" +
            "FROM\n" +
            "\tsys_origin ")
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

    @Delete("delete from sys_origin where origin_id = #{originId}")
    void deleteById(@Param("originId")String origin_id);

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

    @Select("select origin_id,origin_name,parent_origin_id,origin_status,create_date,create_user from sys_origin")
    Page<Submitauthority> listSubmitauthority(@Param("currPage") int currPage, @Param("pageSize") int pageSize);

}
