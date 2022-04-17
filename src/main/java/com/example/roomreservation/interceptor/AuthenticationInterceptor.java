package com.example.roomreservation.interceptor;

import com.alibaba.fastjson.JSON;
import com.example.roomreservation.annotation.AdminToken;
import com.example.roomreservation.annotation.PassToken;
import com.example.roomreservation.annotation.UserToken;
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
        try (PrintWriter out = response.getWriter()) {
            out.append(JSON.toJSONString(result));
        } catch (IOException e) {
            e.printStackTrace();
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

        // todo 用户和管理员都能访问的接口
        if (method.isAnnotationPresent(UserToken.class) && method.isAnnotationPresent(AdminToken.class)) {
            log.info("user and admin token");

            if (token == null) {
                log.warn("not login");
                result(httpServletResponse, JsonResult.error(201, "账户未登录"));
                return false;
            }

            Map<String, Integer> map = JWTUtil.parseToken(token);
            if (map.size() == 0) {
                log.warn("token error");
                result(httpServletResponse, JsonResult.error(203, "token信息错误"));
                return false;
            }
            log.info("type={}", map.get("type"));
            BaseContext.setCurrent(map);

            return true;
        }

        // 管理员权限检查
        if (method.isAnnotationPresent(AdminToken.class)) {
            AdminToken adminToken = method.getAnnotation(AdminToken.class);
            log.info("admin token");
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
                BaseContext.setCurrent(map);

                return true;
            }
        }

        // todo 如何从token中区分用户和管理员
        if (method.isAnnotationPresent(UserToken.class)) {
            UserToken userToken = method.getAnnotation(UserToken.class);
            log.info("user token");
            if (userToken.required()) {
                // 执行认证
                if (token == null) {
                    log.warn("user not login");
                    result(httpServletResponse, JsonResult.error(201, "账户未登录"));
                    return false;
                }

                Map<String, Integer> map = JWTUtil.parseToken(token);
                if (map.size() == 0) {
                    log.warn("user token error");
                    result(httpServletResponse, JsonResult.error(203, "token信息错误"));
                    return false;
                }
                BaseContext.setCurrent(map);

                return true;
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}