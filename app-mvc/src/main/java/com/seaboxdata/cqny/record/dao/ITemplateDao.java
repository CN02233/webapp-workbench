package com.seaboxdata.cqny.record.dao;

import com.github.pagehelper.Page;
import com.seaboxdata.cqny.record.entity.ExcelTemplate;
import com.seaboxdata.cqny.record.entity.ExcelTemplateCell;
import com.seaboxdata.cqny.record.entity.ExcelTemplateCellMerged;
import com.seaboxdata.cqny.record.entity.ExcelTemplateSheet;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ITemplateDao {

    @Select("select * from report_template")
    Page<ExcelTemplate> pagerTemplates(@Param("currPage") int currPage, @Param("pageSize") int pageSize);

    @Select("select *" +
            " from report_template where " +
            "template_id = #{templateId} ")
    @Results(value={
            @Result(property = "template_id",column = "template_id"),
            @Result(property = "excelTemplateSheets",column = "template_id" ,javaType= List.class, many=@Many(select="getExcelTemplateSheet"))
    })
    List<ExcelTemplate> getExcelTemplate(String templateId);

    @Select("select * from report_template where template_id = #{templateId} ")
    List<ExcelTemplate> getExcelTemplateBasic(String templateId);


    @Select("select * from report_template_sheet where template_id=#{templateId}")
    @Results(value={
            @Result(property = "id",column = "id"),
            @Result(property = "excelTemplateCells",column = "id" ,javaType= List.class, many=@Many(select="getCellContext")),
            @Result(property = "excelTemplateCellMergeds",column = "id" ,javaType= List.class, many=@Many(select="getAllMerged"))
    })
    List<ExcelTemplateSheet> getExcelTemplateSheet(String templateId);

    @Select("select * from report_template_context where sheet_id=#{sheetId}")
    List<ExcelTemplateCell> getCellContext(String sheetId);

    @Select("select * from report_template_merged where sheet_id=#{sheetId}")
    List<ExcelTemplateCellMerged> getAllMerged(String sheetId);

    @Insert("insert into report_template (template_name,template_source_file,source_file_name,import_user,import_date) " +
            "values (#{template_name},#{template_source_file},#{source_file_name},#{import_user},#{import_date})")
    @Options(useGeneratedKeys = true, keyProperty = "template_id", keyColumn = "template_id")
    void saveTemplate(ExcelTemplate excelTemplate);

    @Insert("insert into report_template_sheet (template_id,sheet_num,sheet_name) values (#{template_id},#{sheet_num},#{sheet_name})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void saveTemplateSheet(ExcelTemplateSheet excelTemplateSheet);

    @Insert("insert into report_template_context (sheet_id,template_row,template_col,template_context,template_col_styles) values " +
            "(#{sheet_id},#{template_row},#{template_col},#{template_context},#{template_col_styles})")
    void saveTemplateCell(ExcelTemplateCell excelTemplateCell);

    @Insert("insert into report_template_merged (sheet_id,row,col,rowspan,colspan) values " +
            "(#{sheet_id},#{row},#{col},#{rowspan},#{colspan})")
    void saveExcelTemplateCellMerged(ExcelTemplateCellMerged mergedData);

}
