package com.example.roomreservation.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 异常处理方法
     *
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public JsonResult<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.error(ex.getMessage());
        if (ex.getMessage().contains("Duplicate entry")) {
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return JsonResult.error(msg);
        }
        return JsonResult.error("未知错误");
    }

    @ExceptionHandler(CustomException.class)
    public JsonResult<String> exceptionHandler(CustomException ex) {
        log.error(ex.getMessage());
        if (ex.getCode() != null) {
            return JsonResult.error(ex.getCode(), ex.getMessage());
        }
        return JsonResult.error(ex.getMessage());
    }
}