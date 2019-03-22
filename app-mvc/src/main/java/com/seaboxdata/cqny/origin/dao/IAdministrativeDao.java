package com.seaboxdata.cqny.origin.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.origin.entity.Administrative;
import com.seaboxdata.cqny.origin.test.EntityTree;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAdministrativeDao {

    @Insert("INSERT INTO organizations (\n" +
            "\torganization_name,\n" +
            "\tcreate_time,\n" +
            "\tcreate_user,\n" +
            "\torigin_id\n" +
            ")\n" +
            "VALUES\n" +
            "\t(\n" +
            "\t#{ organization_name },sysdate(),#{ create_user },#{ origin_id }\n" +
            "\t)")
    void addAdministrative(Administrative administrative);

    @Delete("delete from organizations where organization_id = #{organizationId}")
    void deleteById(@Param("organizationId") String organization_id);

    @Update("<script>update organizations <set>"
            +"<if test='origin_id!=null'>"
            +"origin_id=#{origin_id} ,"
            +"</if>"
            +"<if test='organization_name!=null'>"
            +"organization_name=#{organization_name} ,"
            +"</if>"
            +"<if test='create_user!=null'>"
            +"create_user=#{create_user} ,"
            +"</if>"
            +"create_time=sysdate() "
            +"</set>where organization_id = #{organization_id}</script>")
    void updateAdministrative(Administrative administrative);

    @Select("SELECT\n" +
            "\ta.organization_id,\n" +
            "\ta.organization_name,\n" +
            "\ta.origin_id,\n" +
            "\ta.create_time,\n" +
            "\ta.create_user,\n" +
            "\tb.origin_name\n" +
            "FROM\n" +
            "\torganizations a LEFT JOIN sys_origin b on a.origin_id=b.origin_id")
    Page<Administrative> listAdministrative(@Param("currPage") int currPage, @Param("pageSize") int pageSize);

}
