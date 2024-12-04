package com.cbx.intelliedu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cbx.intelliedu.model.dto.answerrecord.AppAnswerCountDTO;
import com.cbx.intelliedu.model.entity.AnswerRecord;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author passion
 * @description 针对表【answer_record(Answer Record)】的数据库操作Mapper
 * @createDate 2024-10-15 16:41:48
 * @Entity generator.domain.AnswerRecord
 */
public interface AnswerRecordMapper extends BaseMapper<AnswerRecord> {
    @Select("select distinct (user_id) from intelliedu.answer_record where app_id = #{appId}")
    List<Long> getUserIdListByAppId(Long appId);

    /**
     * Returns distinct number of users who have answered specific apps
     */
    @Select("select answer_record.app_id, count(answer_record.app_id) as answerCount\n" +
            "from answer_record\n" +
            "group by app_id\n" +
            "order by answerCount desc;")
    List<AppAnswerCountDTO> getUserAnswerCountList();


}




