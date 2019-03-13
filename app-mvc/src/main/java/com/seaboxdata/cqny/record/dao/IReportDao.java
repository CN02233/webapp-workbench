package com.seaboxdata.cqny.record.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface IReportDao {

    static final String select_column = "SELECT " +
            "ri.report_id as reportId," +
            "ri.template_id as reportTemplateId," +
            "ri.report_name as reportName," +
            "ri.report_status as reportStatus," +
            "ri.report_lock_user as lockedUser," +
            "ri.report_creater as reportCreate, " +
            "ri.report_create_time as reportCreateDate " +
            "FROM report_infos ri ";

    @Select("SELECT " +
            "ri.report_id as reportId," +
            "ri.template_id as reportTemplateId," +
            "ri.report_name as reportName," +
            "ri.report_status as reportStatus," +
            "ri.report_lock_user as lockedUser," +
            "(select user_name from user where user_id = ri.report_lock_user) as lockUserName, " +
            "ri.report_creater as reportCreate, " +
            "ur.user_name as reportCreateName, " +
            "ri.report_create_time as reportCreateDate " +
            "FROM report_infos ri inner join user ur on ri.report_creater = ur.user_id")
    @Options(useCache = false)
    Page<ReportInfo> reportList(@Param("currPage") int currPage, @Param("pageSize") int pageSize,@Param("user_id")int  user_id);

    @Insert("insert into report_infos (report_name,report_path,report_creater,report_create_time,template_id) values " +
            "(#{reportName},#{reportPath},#{reportCreate},#{reportCreateDate},#{reportTemplateId})")
    @Options(useGeneratedKeys = true, keyProperty = "reportId", keyColumn = "report_id")
    int createReport(ReportInfo reportInfo);

    @Insert("insert into report_context  " +
            "(report_id,template_id,template_sheet_id,report_row,report_colum,report_context)   " +
            "select res.* from "+
            "(select #{reportId} as reportId,#{templateId} as templateId,rt.sheet_id,rt.template_row,rt.template_col,rt.template_context from " +
            "report_template_context rt inner join report_template_sheet rts on " +
            "rts.template_id = #{templateId} and rt.sheet_id = rts.id) as res")
    void copyTemplateContext(@Param("reportId") Integer reportId,@Param("templateId") Integer templateId);

    @Insert("insert into report_context_merged (sheet_id,row,col,colspan,rowspan) " +
            "select res.* from (" +
            "select rtm.sheet_id,rtm.row,rtm.col,rtm.colspan,rtm.rowspan from report_template_merged rtm inner join report_template_sheet rts on " +
            " rts.template_id   and  rtm.sheet_id = rts.id" +
            ") as res")
    void copyTemplateMerged(@Param("templateId") Integer templateId);

    @Select(select_column+ " where ri.report_id=#{reportIdOrName}")
    @Results(value={
            @Result(property = "reportTemplateId",column = "reportTemplateId"),
            @Result(property = "excelReportSheets",column = "reportTemplateId" ,javaType= List.class, many=@Many(select="getExcelTemplateSheet"))
    })
    ReportInfo getReportInfoById(String reportIdOrName);


    @Select(select_column+ " where ri.report_id=#{reportIdOrName}")
    ReportInfo getBasicReportInfo(String reportIdOrName);

    @Select("select * from report_template_sheet where template_id=#{templateId}")
    @Results(value={
            @Result(property = "id",column = "id"),
            @Result(property = "excelReportCells",column = "id" ,javaType= List.class, many=@Many(select="getReportCellContext")),
            @Result(property = "excelTemplateCellMergeds",column = "id" ,javaType= List.class, many=@Many(select="getAllMerged")),
            @Result(property = "excelCopyGroup",column = "id" ,javaType= List.class, many=@Many(select="getSheetCopyGroups"))
    })
    List<ExcelReportSheet> getExcelTemplateSheet(String templateId);

    @Select("select * from report_context where template_sheet_id=#{sheetId}")
    List<ExcelReportCell> getReportCellContext(String sheetId);

    @Select("select * from report_context_merged where sheet_id=#{sheetId}")
    List<ExcelTemplateCellMerged> getAllMerged(String sheetId);

    @Select("select * from report_template_copys where sheet_id=#{sheetId}")
    List<ExcelCopyGroup> getSheetCopyGroups(String sheetId);

    @Update("update report_context set report_context=#{context} where report_id=#{reportId} and template_sheet_id=#{sheetId} and report_row=#{rowNum} and report_colum=#{columNum}")
    void updateReport(
            @Param("reportId") String reportId,
            @Param("sheetId") String sheetId,
            @Param("rowNum") Integer rowNum,
            @Param("columNum") Integer columNum,
            @Param("context") String context);

    @Update("update report_infos set report_status=#{status} " +
            ",report_lock_user=#{lockUser} "+
            "where report_id=#{reportId} ")
    void changeReportStatus(@Param("status") String status,@Param("reportId") String reportId,@Param("lockUser") Integer lockUser);

    @Update("update report_infos set report_status=#{status} " +
            ",review_user=#{reviewUser} "+
            "where report_id=#{reportId} ")
    void reviewReport(@Param("status") String status,@Param("reportId") String reportId,@Param("reviewUser") Integer reviewUser);

    @Update("update report_infos set report_status=#{status} " +
            ",confirm_user=#{reviewUser} "+
            "where report_id=#{reportId} ")
    void confirmReport(@Param("status") String status,@Param("reportId") String reportId,@Param("reviewUser") Integer reviewUser);

    @Delete("delete from ")
    void removeOldReportInfo(String reportId, String sheetId);

    @Delete("delete from report_context where report_id=#{reportId} and template_sheet_id=#{sheetId}")
    void removeReportContext(@Param("reportId") String reportId,@Param("sheetId") String sheetId);

    @Delete("delete from report_context_merged where sheet_id=#{sheetId}")
    void removeReportMerged(@Param("reportId") String reportId,@Param("sheetId") String sheetId);

//    (report_id,template_id,template_sheet_id,report_row,report_colum,report_context)
    @Insert("insert into report_context (report_id,template_id,template_sheet_id,report_row,report_colum,report_context) values " +
            "(#{report_id},#{template_id},#{template_sheet_id},#{report_row},#{report_colum},#{report_context})")
    void insertReportContext(Map<String,Object> paramMap);

    @Insert("insert into report_context_merged (sheet_id,row,col,colspan,rowspan) values " +
            "(#{sheet_id},#{row},#{col},#{colspan},#{rowspan})")
    void insertReportMerged(Map<String,String> paramMap);
}
