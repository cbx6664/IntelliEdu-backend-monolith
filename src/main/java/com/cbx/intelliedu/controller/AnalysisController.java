package com.cbx.intelliedu.controller;

import com.cbx.intelliedu.auth.annotation.RequiresAdmin;
import com.cbx.intelliedu.common.response.ApiResponse;
import com.cbx.intelliedu.mapper.AnswerRecordMapper;
import com.cbx.intelliedu.model.dto.answerrecord.AppAnswerCountDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * ClassName: AnalysisController
 * Package: com.cbx.intelliedu.controller
 * Description:
 *
 * @Author CBX
 * @Create 4/12/24 14:19
 * @Version 1.0
 */
@RestController
@RequestMapping("/analysis")
public class AnalysisController {

    @Resource
    AnswerRecordMapper answerRecordMapper;

    // 管理员查看用户答题分布
    @GetMapping("/userAnswerCount")
    @RequiresAdmin
    public ApiResponse<List<AppAnswerCountDTO>> listAnswerCount() {
        return ApiResponse.success(answerRecordMapper.getUserAnswerCountList());
    }
}
