package com.example.roomreservation.interceptor;

import com.alibaba.fastjson.JSON;
import com.example.roomreservation.annotation.AdminToken;
import com.example.roomreservation.annotation.PassToken;
import com.example.roomreservation.common.BaseContext;
import com.example.roomreservation.common.JsonResult;
import com.example.roomreservation.util.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    /**
     * 以字符流的方式向响应体中写入json
     * @param response
     * @param result
     */
    private void result(HttpServletResponse response, JsonResult result) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(200);
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.append(JSON.toJSONString(result));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(object instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) object;
        Method method = handlerMethod.getMethod();

        // 检查是否有passtoken注释，有则跳过认证
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.required()) {
                log.info("pass token");
                return true;
            }
        }
        String authorization = httpServletRequest.getHeader("Authorization");
        if (authorization == null || !authorization.contains("Bearer ")) {
            result(httpServletResponse, JsonResult.error(201, "账户未登录"));
            return false;
        }
        String token = authorization.split(" ")[1];
        log.info("token: {}", token);
        // 管理员权限检查
        if (method.isAnnotationPresent(AdminToken.class)) {
            AdminToken adminToken = method.getAnnotation(AdminToken.class);
            if (adminToken.required()) {
                // 执行认证
                if (token == null) {
                    log.warn("admin not login");
                    result(httpServletResponse, JsonResult.error(201, "账户未登录"));
                    return false;
                }

                Map<String, Integer> map = JWTUtil.parseToken(token);
                if (map.size() == 0) {
                    log.warn("admin token error");
                    result(httpServletResponse, JsonResult.error(203, "token信息错误"));
                    return false;
                }

                Integer id = map.get("id");
                BaseContext.setCurrentId(id);

                return true;
            }
        }

        // todo 用户权限检查
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}