package com.seaboxdata.cqny.record.service.imp;

import com.seaboxdata.cqny.record.config.RecordConfig;
import com.seaboxdata.cqny.record.dao.ITemplateDao;
import com.seaboxdata.cqny.record.entity.ExcelTemplate;
import com.seaboxdata.cqny.record.service.TemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("template")
public class TemplateServiceImp implements TemplateService {

    private Logger logger = LoggerFactory.getLogger(TemplateServiceImp.class);

    @Autowired
    private RecordConfig recordConfig;

    @Autowired
    private ITemplateDao templateDao;

    @Override
    public List<Map<String,String>> loadTemplate(String templateIdOrName) {

        ExcelTemplate templateInfo = templateDao.getExcelTemplate(templateIdOrName);

//        List<Map<String,String>> templateContent = this.readFile(loadTemplateFileFromDisk(templateIdOrName));

//        return templateContent;
            return null;
    }


}
