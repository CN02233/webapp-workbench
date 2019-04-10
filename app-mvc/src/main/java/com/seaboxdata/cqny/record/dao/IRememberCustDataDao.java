package com.seaboxdata.cqny.record.dao;

import com.seaboxdata.cqny.record.entity.RememberCustData;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface IRememberCustDataDao {

    @Select("select * from report_remembers where unit_id = #{unitId} and user_id = #{userId}")
    List<RememberCustData> getUserRemeberCustDatasByUnit(@Param("unitId") String unitId,@Param("userId") String userId);

    @Select("select * from report_remembers where (colum_id = #{columOrDimId} or dimensions_id=#{dimensions_id})" +
            " and user_id = #{userId}")
    List<RememberCustData> getUserRemeberCustDatasByColum(@Param("columOrDimId") String columOrDimId,@Param("userId") String userId);

    @Select("select * from report_remembers where " +
            "colum_id = #{columOrDimId} and dimensions_id=#{dimensions_id} " +
            " and user_id = #{userId}")
    List<RememberCustData> getUserRemeberCustDatasByColumAndDim(@Param("columOrDimId") String columOrDimId,@Param("userId")  String userId);

    @Delete("delete from report_remembers where report_id=#{reportId} and user_id=#{userId} and unit_id =#{unitId}")
    void deleteRememberCustDataByUnit(@Param("reportId") String reportId,@Param("unitId") String unitId,@Param("userId") String userId);

    @Delete("delete from report_remembers where report_id=#{reportId} and user_id=#{userId} and " +
            "(colum_id = #{columOrDimId} or dimensions_id=#{dimensions_id})")
    void deleteRememberCustDataByColum(@Param("reportId") String reportId,@Param("columOrDimId")  String columOrDimId);

    @Insert("insert into report_remembers (report_id,user_id,unit_id,colum_id,dimensions_id,remember_data) values " +
            "(#{report_id},#{user_id},#{unit_id},#{colum_id},#{dimensions_id},#{remember_data})")
    void saveRememberCustData(RememberCustData rememberCustData);
}
