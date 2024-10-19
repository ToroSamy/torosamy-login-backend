package net.torosamy.main.handler;

import lombok.extern.slf4j.Slf4j;

import net.torosamy.main.constant.MessageConstant;
import net.torosamy.main.domain.vo.Result;
import net.torosamy.main.exception.BaseException;
import net.torosamy.main.exception.TokenNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("Base Error Message: {}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    public Result exceptionHandler(TokenNotFoundException ex){
        return Result.error();
    }

    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        String message = ex.getMessage();
        if (message.contains("user.username"))
            return Result.error(MessageConstant.ACCOUNT_ALREADY_EXISTS);
        else if(message.contains("user.qq") )
            return Result.error(MessageConstant.QQ_ALREADY_EXISTS);

        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

}
