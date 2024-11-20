package com.cpuoverload.intelliedu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cpuoverload.intelliedu.common.dto.IdRequest;
import com.cpuoverload.intelliedu.common.response.ApiResponse;
import com.cpuoverload.intelliedu.exception.BusinessException;
import com.cpuoverload.intelliedu.exception.Err;
import com.cpuoverload.intelliedu.model.dto.application.*;
import com.cpuoverload.intelliedu.model.entity.Application;
import com.cpuoverload.intelliedu.model.vo.ApplicationVo;
import com.cpuoverload.intelliedu.service.ApplicationService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/application")
public class ApplicationController {

    @Resource
    private ApplicationService applicationService;

    // 查看主页的应用列表
    @PostMapping("/list/public")
    public ApiResponse<Page<ApplicationVo>> listPublicApplication(@RequestBody ListPublicAppRequest listPublicAppRequest) {
        if (listPublicAppRequest == null) {
            throw new BusinessException(Err.PARAMS_ERROR);
        }
        Page<ApplicationVo> applicationVoPage = applicationService.listPublicApplication(listPublicAppRequest);
        return ApiResponse.success(applicationVoPage);
    }

    // 普通用户创建应用
    @PostMapping("/add/me")
    public ApiResponse<Long> addMyApplication(@RequestBody AddMyAppRequest addMyAppRequest, HttpServletRequest request) {
        if (addMyAppRequest == null) {
            throw new BusinessException(Err.PARAMS_ERROR);
        }
        if (addMyAppRequest.getAppName() == null || addMyAppRequest.getType() == null || addMyAppRequest.getStrategy() == null) {
            throw new BusinessException(Err.PARAMS_ERROR);
        }
        Application application = new Application();
        BeanUtils.copyProperties(addMyAppRequest, application);
        Long appId = applicationService.addMyApplication(application, request);
        return ApiResponse.success(appId);
    }

    // 普通用户查看自己的应用列表
    @PostMapping("/list/me")
    public ApiResponse<Page<ApplicationVo>> listMyApplication(@RequestBody ListMyAppRequest listMyAppRequest, HttpServletRequest request) {
        if (listMyAppRequest == null) {
            throw new BusinessException(Err.PARAMS_ERROR);
        }
        Page<ApplicationVo> applicationVoPage = applicationService.listMyApplication(listMyAppRequest, request);
        return ApiResponse.success(applicationVoPage);
    }

    // 普通用户更新应用
    @PostMapping("/update/me")
    public ApiResponse<Boolean> updateMyApplication(@RequestBody UpdateMyAppRequest updateMyAppRequest, HttpServletRequest request) {
        if (updateMyAppRequest == null) {
            throw new BusinessException(Err.PARAMS_ERROR);
        }
        Application application = new Application();
        BeanUtils.copyProperties(updateMyAppRequest, application);
        Boolean success = applicationService.updateMyApplication(application, request);
        if (!success) {
            throw new BusinessException(Err.SYSTEM_ERROR);
        }
        return ApiResponse.success(true);
    }

    // 普通用户删除应用
    @PostMapping("/delete/me")
    public ApiResponse<Boolean> deleteMyApplication(@RequestBody IdRequest idRequest, HttpServletRequest request) {
        if (idRequest == null) {
            throw new BusinessException(Err.PARAMS_ERROR);
        }
        Boolean success = applicationService.deleteMyApplication(idRequest, request);
        if (!success) {
            throw new BusinessException(Err.SYSTEM_ERROR);
        }
        return ApiResponse.success(true);
    }

    // 管理员查看应用列表
    @PostMapping("/list")
    public ApiResponse<Page<ApplicationVo>> listApplication(@RequestBody ListAppRequest listAppRequest) {
        if (listAppRequest == null) {
            throw new BusinessException(Err.PARAMS_ERROR);
        }
        Page<ApplicationVo> applicationVoPage = applicationService.listApplication(listAppRequest);
        return ApiResponse.success(applicationVoPage);
    }

    // 管理员更新应用
    @PostMapping("/update")
    public ApiResponse<Boolean> updateApplication(@RequestBody UpdateAppRequest updateAppRequest) {
        if (updateAppRequest == null || updateAppRequest.getId() == null) {
            throw new BusinessException(Err.PARAMS_ERROR);
        }
        Application application = new Application();
        BeanUtils.copyProperties(updateAppRequest, application);
        Boolean success = applicationService.updateApplication(application);
        if (!success) {
            throw new BusinessException(Err.SYSTEM_ERROR);
        }
        return ApiResponse.success(true);
    }

    // 管理员删除应用
    @PostMapping("/delete")
    public ApiResponse<Boolean> deleteApplication(@RequestBody IdRequest idRequest) {
        if (idRequest == null) {
            throw new BusinessException(Err.PARAMS_ERROR);
        }
        Boolean success = applicationService.deleteApplication(idRequest);
        if (!success) {
            throw new BusinessException(Err.SYSTEM_ERROR);
        }
        return ApiResponse.success(true);
    }

    // 管理员审核应用
    @PostMapping("/audit")
    public ApiResponse<Boolean> auditApplication(@RequestBody AuditAppRequest auditAppRequest, HttpServletRequest request) {
        if (auditAppRequest == null) {
            throw new BusinessException(Err.PARAMS_ERROR);
        }
        Application application = new Application();
        BeanUtils.copyProperties(auditAppRequest, application);
        Boolean success = applicationService.auditApplication(application, request);
        if (!success) {
            throw new BusinessException(Err.SYSTEM_ERROR);
        }
        return ApiResponse.success(true);
    }

    @GetMapping("/get/{id}")
    public ApiResponse<Application> getApplicationById(@PathVariable Long id){
        return ApiResponse.success(applicationService.getApplicationById(id));
    }

}
