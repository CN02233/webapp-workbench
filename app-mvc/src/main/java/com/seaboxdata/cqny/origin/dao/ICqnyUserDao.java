package com.seaboxdata.cqny.origin.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.origin.entity.CqnyUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ICqnyUserDao {
    @Select("<script>" +
            " select u.*,so.origin_id,so.origin_name  from user u,user_origin_assign uoa,sys_origin so " +
            " where u.user_id =uoa.user_id and uoa.origin_id=so.origin_id " +
            " <if test='user_type!=null'> and u.user_type = #{user_type} </if>" +
            " <if test='user_type=null'> and (u.user_type='1' or u.user_type='0')  </if>" +
            " <if test='user_name_cn!=null'> and u.user_name_cn like concat('%',#{user_name_cn},'%') </if>" +
            " <if test='seachOrigins!=null'> and so.origin_id in  " +
            "  <foreach item=\"item\" index=\"index\" collection=\"seachOrigins\" open=\"(\" separator=\",\" close=\")\">" +
            "  #{item}" +
            "  </foreach>"+
            " </if>" +
            "</script>" )
    Page<CqnyUser> pageCqnyUser(@Param("currPage") Integer currPage,
                                @Param("pageSize") Integer pageSize,
                                @Param("user_name_cn") String user_name_cn,
                                @Param("user_type") String user_type,
                                @Param("seachOrigins") List<Integer> seachOrigins);
}
