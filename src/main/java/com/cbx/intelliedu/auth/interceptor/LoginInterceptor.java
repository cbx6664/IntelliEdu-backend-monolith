package com.cbx.intelliedu.auth.interceptor;

import com.cbx.intelliedu.auth.annotation.RequiresLogin;
import com.cbx.intelliedu.constant.UserConstant;
import com.cbx.intelliedu.exception.Err;
import com.cbx.intelliedu.model.vo.UserVo;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RequiresLogin annotation = handlerMethod.getMethodAnnotation(RequiresLogin.class);
            if (annotation != null) {
                UserVo user = (UserVo) request.getSession().getAttribute(UserConstant.LOGIN_USER);
                if (user == null) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write("{\"code\": \"" + Err.UNLOGIN_ERROR.getCode() + "\"}");
                    return false;
                }
            }
        }
        return true;
    }
}
