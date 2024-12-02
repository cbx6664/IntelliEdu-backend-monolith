package com.cbx.intelliedu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cbx.intelliedu.common.dto.IdRequest;
import com.cbx.intelliedu.common.response.ApiResponse;
import com.cbx.intelliedu.exception.BusinessException;
import com.cbx.intelliedu.exception.Err;
import com.cbx.intelliedu.manager.AiManager;
import com.cbx.intelliedu.model.dto.question.*;
import com.cbx.intelliedu.model.entity.Application;
import com.cbx.intelliedu.model.entity.Question;
import com.cbx.intelliedu.model.enums.AppType;
import com.cbx.intelliedu.model.vo.QuestionVo;
import com.cbx.intelliedu.service.ApplicationService;
import com.cbx.intelliedu.service.QuestionService;
import com.cbx.intelliedu.service.UserService;
import dev.ai4j.openai4j.chat.ChatCompletionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

import static com.cbx.intelliedu.constant.AIConstant.GENERATE_EVALUATION_QUESTION_SYSTEM_MESSAGE;
import static com.cbx.intelliedu.constant.AIConstant.GENERATE_GRADE_QUESTION_SYSTEM_MESSAGE;


@RestController
@RequestMapping("/application/question")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private ApplicationService applicationService;

    @Resource
    private UserService userService;


    @Resource
    AiManager aiManager;

    // 获取一个应用的题目列表（不要分页）
    @PostMapping("/get/public")
    public ApiResponse<QuestionVo> getPublicQuestionOfOneApp(@RequestBody GetPublicQuestionRequest getPublicQuestionRequest) {
        if (getPublicQuestionRequest == null) {
            throw new BusinessException(Err.PARAMS_ERROR);
        }
        QuestionVo questionVo = questionService.getPublicQuestion(getPublicQuestionRequest);
        return ApiResponse.success(questionVo);
    }

    // 普通用户创建题目
    @PostMapping("/add/me")
    public ApiResponse<Boolean> addMyQuestion(@RequestBody AddMyQuestionRequest addMyQuestionRequest, HttpServletRequest request) {
        if (addMyQuestionRequest == null) {
            throw new BusinessException(Err.PARAMS_ERROR);
        }
        if (addMyQuestionRequest.getQuestions() == null || addMyQuestionRequest.getAppId() == null) {
            throw new BusinessException(Err.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(addMyQuestionRequest, question);
        Boolean success = questionService.addMyQuestion(question, request);
        if (!success) {
            throw new BusinessException(Err.SYSTEM_ERROR);
        }
        return ApiResponse.success(true);
    }

    // 普通用户查看自己的题目（只允许每次查询一个应用的题目，不要分页）
    @PostMapping("/get/me")
    public ApiResponse<QuestionVo> getMyQuestionOfOneApp(@RequestBody GetMyQuestionRequest getMyQuestionRequest, HttpServletRequest request) {
        if (getMyQuestionRequest == null) {
            throw new BusinessException(Err.PARAMS_ERROR);
        }
        QuestionVo questionVo = questionService.getMyQuestion(getMyQuestionRequest, request);
        return ApiResponse.success(questionVo);
    }

    // 普通用户更新题目
    @PostMapping("/update/me")
    public ApiResponse<Boolean> updateMyQuestion(@RequestBody UpdateMyQuestionRequest updateMyQuestionRequest, HttpServletRequest request) {
        if (updateMyQuestionRequest == null) {
            throw new BusinessException(Err.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(updateMyQuestionRequest, question);
        Boolean success = questionService.updateMyQuestion(question, request);
        if (!success) {
            throw new BusinessException(Err.SYSTEM_ERROR);
        }
        return ApiResponse.success(true);
    }

    // 普通用户删除题目
    @PostMapping("/delete/me")
    public ApiResponse<Boolean> deleteMyQuestion(@RequestBody IdRequest idRequest, HttpServletRequest request) {
        if (idRequest == null) {
            throw new BusinessException(Err.PARAMS_ERROR);
        }
        Boolean success = questionService.deleteMyQuestion(idRequest, request);
        if (!success) {
            throw new BusinessException(Err.SYSTEM_ERROR);
        }
        return ApiResponse.success(true);
    }

    // 管理员查看题目列表
    @PostMapping("/list")
    public ApiResponse<Page<QuestionVo>> listQuestion(@RequestBody ListQuestionRequest listQuestionRequest) {
        if (listQuestionRequest == null) {
            throw new BusinessException(Err.PARAMS_ERROR);
        }
        Page<QuestionVo> questionVoPage = questionService.listQuestion(listQuestionRequest);
        return ApiResponse.success(questionVoPage);
    }

    // 管理员更新题目
    @PostMapping("/update")
    public ApiResponse<Boolean> updateQuestion(@RequestBody UpdateQuestionRequest updateQuestionRequest) {
        if (updateQuestionRequest == null || updateQuestionRequest.getId() == null) {
            throw new BusinessException(Err.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(updateQuestionRequest, question);
        Boolean success = questionService.updateQuestion(question);
        if (!success) {
            throw new BusinessException(Err.SYSTEM_ERROR);
        }
        return ApiResponse.success(true);
    }

    // 管理员删除题目
    @PostMapping("/delete")
    public ApiResponse<Boolean> deleteQuestion(@RequestBody IdRequest idRequest) {
        if (idRequest == null) {
            throw new BusinessException(Err.PARAMS_ERROR);
        }
        Boolean success = questionService.deleteQuestion(idRequest);
        if (!success) {
            throw new BusinessException(Err.SYSTEM_ERROR);
        }
        return ApiResponse.success(true);
    }

    @GetMapping("/get/{appId}")
    public ApiResponse<Question> getQuestionByAppId(@PathVariable Long appId) {
        return ApiResponse.success(questionService.getQuestionByAppId(appId));
    }


    //region ai生成题目


    /**
     * 生成题目的用户消息
     *
     * @param application
     * @param questionNumber
     * @param optionNumber
     * @return
     */
    private String getGenerateQuestionUserMessage(Application application, int questionNumber, int optionNumber) {
        String userMessage = "Application name: " + application.getAppName() + "\n" + "Application description: " + application.getDescription() + "\n" + "Application category: " + AppType.fromCode(application.getType()).getDescription() + " type" + "\n" + "Number of questions to generate: " + questionNumber + "Number of options per question: " + optionNumber;
        return userMessage;
    }


    @GetMapping("/ai_generate/sse")
    public SseEmitter aiGenerateQuestionSse(Long appId, Integer questionNumber, Integer optionNumber, HttpServletRequest request) {
        // 调用 AI 生成题目的请求是否为空
        if (appId == null || questionNumber == null || optionNumber == null) {
            throw new BusinessException(Err.PARAMS_ERROR);
        }

        // 获取应用信息
        Application application = applicationService.getById(appId);
        if (application == null) {
            throw new BusinessException(Err.NOT_FOUND_ERROR);
        }

        // 封装prompt
        String userMessage = getGenerateQuestionUserMessage(application, questionNumber, optionNumber);
        String systemMessage = null;

        //测评类应用系统消息
        if (application.getType() == AppType.EVALUATION.getCode()) {
            systemMessage = GENERATE_EVALUATION_QUESTION_SYSTEM_MESSAGE;
        }
        //得分类应用系统消息
        else if (application.getType() == AppType.GRADE.getCode()) {
            systemMessage = GENERATE_GRADE_QUESTION_SYSTEM_MESSAGE;
        }

        ChatCompletionRequest chatCompletionRequest = aiManager.generalStreamRequest(systemMessage, userMessage, 1);
        //建立 sse 连接对象，0表示永不超时
        SseEmitter emitter = new SseEmitter(0L);

        // 处理 AI 生成题目的请求
        CompletableFuture<String> future = new CompletableFuture<>();

        // 获取访问用户的role
        String userRole = userService.getRole(request);
        Boolean isVIP = false;
        if (userRole.equals("admin")) {
            isVIP = true;
        }
        if (userRole.equals("vip")) {
            isVIP = true;
        }

        aiManager.executeChatCompletionWithIsolation(chatCompletionRequest, emitter, future, isVIP);

        return emitter;


    }
    //endregion
}
