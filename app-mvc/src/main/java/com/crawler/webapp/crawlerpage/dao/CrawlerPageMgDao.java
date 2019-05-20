package com.crawler.webapp.crawlerpage.dao;

import com.crawler.webapp.crawlerpage.bean.CrawlerPage;
import com.crawler.webapp.crawlerpage.bean.PageField;
import com.crawler.webapp.crawlerpage.bean.PageFieldLocate;
import com.crawler.webapp.crawlerpage.bean.PageLink;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by SongCQ on 2017/7/25.
 */
public interface CrawlerPageMgDao {

    @Select("select * from crawl_page_config")
    @Options(useCache = false)
    Page<CrawlerPage> listCrawlerPageByPaging(@Param("currPage") int currPage, @Param("pageSize") int pageSize);

    @Select("select * from crawl_page_config where page_id=#{page_id} and job_id=#{job_id} and user_id=#{user_id}")
    @Options(useCache = false)
    CrawlerPage craPageData(@Param("page_id") int page_id,@Param("job_id") int job_id,@Param("user_id") int user_id);

    @Select("select * from page_link where page_id=#{page_id} and job_id=#{job_id} and user_id=#{user_id}")
    @Options(useCache = false)
    List<PageLink> listPageLink(@Param("page_id") int page_id,@Param("job_id") int job_id,@Param("user_id") int user_id);

//    @Select("select pf.*,pfl.* from page_field pf inner join page_field_locate_relation pflr on" +

    @Select("select pf.*,pflr.field_locate_id from page_field pf inner join page_field_locate_relation pflr on " +
            " pf.page_id=pflr.page_id and pf.job_id=pflr.job_id and pf.user_id=pflr.user_id and pf.field_id=pflr.field_id and " +
            " pf.page_id=#{page_id} and pf.job_id=#{job_id} and pf.user_id=#{user_id}")
    @Results({@Result(property = "pageFieldLocate",column = "field_locate_id",
            many = @Many(select="com.crawler.webapp.crawlerpage.dao.CrawlerPageMgDao.getPageFieldLocate"))})
    @Options(useCache = false)
    List<PageField> listPageField(@Param("page_id") int page_id, @Param("job_id") int job_id, @Param("user_id") int user_id);

    @Select("select * from page_field_locate where field_locate_id = #{field_locate_id}")
    @Options(useCache = false)
    PageFieldLocate getPageFieldLocate(int field_locate_id);

    @Select("SELECT max(page_id) max_page_id FROM spider_db.crawl_page_config where job_id=#{job_id} and user_id=#{user_id}")
    @Options(useCache = false)
    Integer getMaxPageId(@Param("job_id") int job_id, @Param("user_id") int user_id);

    @Insert("insert into crawl_page_config " +
            "(page_id,job_id,user_id,page_name,page_type,data_format,is_multi_page,paginate_element,load_indicator," +
            "page_interval,max_page_num,save_page_source,data_file) " +
            "values " +
            "(#{page_id},#{job_id},#{user_id},#{page_name},#{page_type},#{data_format},#{is_multi_page},#{paginate_element},#{load_indicator}," +
            "#{page_interval},#{max_page_num},#{save_page_source},#{data_file})")
    @Options(useCache = false)
    void newSaveCrawlerPage(CrawlerPage crawlerPage);

    @Insert("insert into page_field_locate_relation (field_id,page_id,job_id,user_id,field_locate_id) " +
            "values (#{field_id},#{page_id},#{job_id},#{user_id},#{field_locate_id})")
    void savePageFiledLocateRelation(@Param("field_id") int field_id,@Param("page_id") int page_id,@Param("job_id") int job_id,
                                     @Param("user_id") int user_id, @Param("field_locate_id") int field_locate_id);

    @Insert("insert into page_field (page_id,job_id,user_id,field_name,field_datatype,parent_field_id,combine_field_value) " +
            "values (#{page_id},#{job_id},#{user_id},#{field_name},#{field_datatype},#{parent_field_id},#{combine_field_value})")
    @Options(useGeneratedKeys = true,keyProperty = "field_id")
    void savePageField(PageField pageField);

    @Insert("insert into page_field_locate (field_locate_id,field_locate_pattern,field_ext_pattern) " +
            "values (#{field_locate_id},#{field_locate_pattern},#{field_ext_pattern}) ")
    void savePageFieldLocate(PageFieldLocate pageFieldLocate);

    @Insert("insert into page_link (page_id,job_id,user_id,link_locate_pattern,link_ext_pattern,next_page_id) " +
            "values (#{page_id},#{job_id},#{user_id},#{link_locate_pattern},#{link_ext_pattern},#{next_page_id})")
    void savePageLink(PageLink pageLinks);

    @Delete("<script>delete from page_link where page_id=#{page_id} and job_id=#{job_id} and user_id=#{user_id} " +
            "<if test='link_id!=0'> and link_id = #{link_id}</if></script>")
    void removePageLinks(@Param("link_id") int link_id,@Param("page_id") int page_id, @Param("job_id") int job_id, @Param("user_id") int user_id);


    @Update("update crawl_page_config set page_name=#{page_name},page_type=#{page_type},data_format=#{data_format},is_multi_page=#{is_multi_page}," +
            "paginate_element=#{paginate_element},load_indicator=#{load_indicator}," +
            "page_interval=#{page_interval},max_page_num=#{max_page_num},save_page_source=#{save_page_source},data_file=#{data_file} where " +
            "page_id=#{page_id} and job_id=#{job_id} and user_id=#{user_id}")
    void updateCrawlerPage(CrawlerPage crawlerPage);

    @Delete("delete from crawl_page_config where page_id=#{page_id} and job_id=#{job_id} and user_id=#{user_id} ")
    void deleteCrawlerPage(@Param("page_id") int page_id, @Param("job_id") int job_id, @Param("user_id") int user_id);

    @Delete("<script>delete from page_field where page_id=#{page_id} and job_id=#{job_id} and user_id=#{user_id} " +
            "<if test='field_id!=0'> and field_id = #{field_id}</if></script>")
    void removePageFields(@Param("field_id")  int field_id,@Param("page_id") int page_id, @Param("job_id") int job_id, @Param("user_id") int user_id);

    @Delete("delete from page_field_locate_relation where page_id=#{page_id} and job_id=#{job_id} and user_id=#{user_id}")
    void removeLocateRelation(@Param("page_id") int page_id, @Param("job_id") int job_id, @Param("user_id") int user_id);

    @Select("select * from crawl_page_config")
    @Options(useCache = false)
    List<CrawlerPage> listCrawlerPage();

}
