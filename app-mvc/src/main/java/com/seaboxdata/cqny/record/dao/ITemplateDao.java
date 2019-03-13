package com.seaboxdata.cqny.record.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ITemplateDao {

    @Select("select rt.*,us.user_name as import_user_name from report_template rt,user us where rt.import_user = us.user_id")
    Page<ExcelTemplate> pagerTemplates(@Param("currPage") int currPage, @Param("pageSize") int pageSize);

    @Select("select *" +
            " from report_template where " +
            "template_id = #{templateId} ")
    @Results(value={
            @Result(property = "template_id",column = "template_id"),
            @Result(property = "excelTemplateSheets",column = "template_id" ,javaType= List.class, many=@Many(select="getExcelTemplateSheet"))
    })
    ExcelTemplate getExcelTemplate(String templateId);

    @Select("select * from report_template where template_id = #{templateId} ")
    List<ExcelTemplate> getExcelTemplateBasic(String templateId);


    @Select("select * from report_template_sheet where template_id=#{templateId}")
    @Results(value={
            @Result(property = "id",column = "id"),
            @Result(property = "excelTemplateCells",column = "id" ,javaType= List.class, many=@Many(select="getCellContext")),
            @Result(property = "excelTemplateCellMergeds",column = "id" ,javaType= List.class, many=@Many(select="getAllMerged")),
            @Result(property = "excelCopyGroup",column = "id" ,javaType= List.class, many=@Many(select="getSheetCopyGroups"))
    })
    List<ExcelTemplateSheet> getExcelTemplateSheet(String templateId);

    @Select("select * from report_template_context where sheet_id=#{sheetId}")
    List<ExcelTemplateCell> getCellContext(String sheetId);

    @Select("select * from report_template_merged where sheet_id=#{sheetId}")
    List<ExcelTemplateCellMerged> getAllMerged(String sheetId);

    @Select("select * from report_template_copys where sheet_id=#{sheetId}")
    List<ExcelCopyGroup> getSheetCopyGroups(String sheetId);

    @Insert("insert into report_template (template_name,template_source_file,source_file_name,import_user,import_date) " +
            "values (#{template_name},#{template_source_file},#{source_file_name},#{import_user},#{import_date})")
    @Options(useGeneratedKeys = true, keyProperty = "template_id", keyColumn = "template_id")
    void saveTemplate(ExcelTemplate excelTemplate);

    @Insert("insert into report_template_sheet (template_id,sheet_num,sheet_name,row_num,colum_num) " +
            "values (#{template_id},#{sheet_num},#{sheet_name},#{row_num},#{colum_num})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void saveTemplateSheet(ExcelTemplateSheet excelTemplateSheet);

    @Update("update report_template_sheet set row_num=#{row_num},colum_num=#{colum_num} where id=#{id}")
    void updateSheetNums(ExcelTemplateSheet excelTemplateSheet);

    @Insert("insert into report_template_context (sheet_id,template_row,template_col,template_context," +
            "template_col_styles,context_script,context_readonly) values " +
            "(#{sheet_id},#{template_row},#{template_col},#{template_context},#{template_col_styles},#{context_script},#{context_readonly})")
    void saveTemplateCell(ExcelTemplateCell excelTemplateCell);

    @Insert("insert into report_template_merged (sheet_id,row,col,rowspan,colspan) values " +
            "(#{sheet_id},#{row},#{col},#{rowspan},#{colspan})")
    void saveExcelTemplateCellMerged(ExcelTemplateCellMerged mergedData);

    @Select("select distinct rt.* from report_template rt,user_origin_assign uoa,origin_template_assign ota where " +
            "uoa.user_id = #{user_id} and uoa.origin_id = ota.origin_id and ota.template_id = rt.template_id ")
    List<ExcelTemplate> getTemplatesByUser(int user_id);

    @Delete("delete from report_template_copys where template_id = #{reportId} and sheet_id = #{sheetId}")
    void removeCopyGroups(@Param("reportId") String reportId,
                          @Param("sheetId") String sheetId);

    @Insert("insert into report_template_copys (template_id,sheet_id,group_name,group_rows) values " +
            "(#{reportId},#{sheetId},#{copyGroupName},#{groupRows})")
    void saveCopyGroups(@Param("reportId") String reportId,
                        @Param("sheetId") String sheetId,
                        @Param("copyGroupName") String copyGroupName,
                        @Param("groupRows") String groupRows);

    @Delete("delete from report_template_merged where sheet_id in (select id from report_template_sheet where template_id = #{templateId})")
    void removeTemlateMerged(String templateId);

    @Delete("delete from report_template_context where sheet_id in (select id from report_template_sheet where template_id = #{templateId})")
    void removeTemplateContext(String templateId);

    @Delete("delete from report_template_copys where template_id = #{templateId}")
    void removeTemplateCopyGroup(String templateId);

    @Delete("delete from report_template_sheet where template_id = #{templateId}")
    void removeTemplateSheet(String templateId);

    @Delete("delete from report_template where template_id = #{templateId}")
    void removeTemplateInfo(String templateId);
}
