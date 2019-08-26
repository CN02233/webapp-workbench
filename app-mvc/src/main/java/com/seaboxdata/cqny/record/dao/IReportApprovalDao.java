package com.seaboxdata.cqny.record.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.ReportCustomer;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface IReportApprovalDao {

    @Select("<script>SELECT \n" +
            "\trc.active_unit,\n" +
            "\trc.create_date,\n" +
            "\trc.report_status,\n" +
            "\trc.last_modify_user,\n" +
            "\trc.report_defined_id,\n" +
            "\trc.report_end_date,\n" +
            "\trc.report_data_start,\n" +
            "\trc.report_data_end,\n" +
            "\trc.report_id,\n" +
            "\trc.report_name,\n" +
            "\trc.report_origin,\n" +
            "\t(select u.user_name from `user` u where u.user_id=rd.create_user) user_name,\n" +
            "\t uutmp.user_name_cn user_name_cn,uutmp.mobile_phone user_mobile_phone,uutmp.office_phone user_office_phone,\n" +
            "\trc.report_start_date\n" +
            "FROM\n" +
            "\treport_customer rc  LEFT JOIN report_defined rd on rc.report_defined_id=rd.defined_id \n" +
            " LEFT JOIN user uutmp on rc.last_modify_user = uutmp.user_id  " +
            "where \n" +
            " rc.report_status = #{status}\n" +
            "<if test='status==1'>" +
            " and pass_approve = 'N' " +
            "</if>"+
            "<if test='status==2'>" +
            " and pass_review = 'N' " +
            "</if>"+
            "\tand\n" +
            " rc.report_origin IN " +
            "<foreach item='item' index='index' collection='originList' open='(' separator=',' close=')'> " +
            " #{item} " +
            "</foreach>" +
            "</script>")
    Page<ReportCustomer> listReportApproval(@Param("currPage") Integer currPage,
                                            @Param("pageSize") Integer pageSize,
                                            @Param("status") String status,
                                            @Param("originList") List<Integer> originList);

    @Update("<script>update report_customer <set>"
            +"<if test='reportStatus!=null'>"
            +"report_status=#{reportStatus}"
            +"</if>"
            +"</set>where report_id = #{reportId}</script>")
    void reportUpdateStatus(@Param("reportId") String reportId, @Param("reportStatus")String reportStatus);
}
