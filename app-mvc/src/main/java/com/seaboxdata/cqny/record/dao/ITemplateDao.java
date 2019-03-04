package com.seaboxdata.cqny.record.dao;

import com.seaboxdata.cqny.record.entity.ExcelTemplate;
import org.apache.ibatis.annotations.Select;

public interface ITemplateDao {

    @Select("select excelTemplateCell.*,excelTemplateCellMerged.*" +
            " from report_template excelTemplateCell inner join report_template_merged excelTemplateCellMerged on " +
            "excelTemplateCell.template_id = #{templateId} and excelTemplateCell.template_id = excelTemplateCellMerged.template_id")
    ExcelTemplate getExcelTemplate(String templateId);

}
