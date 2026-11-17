package cn.zachqie.scaffold.start.config.aop;

import cn.zachqie.scaffold.type.ResponseResult;
import cn.zachqie.scaffold.type.exceptions.BusinessRuntimeException;
import cn.zachqie.scaffold.type.exceptions.InvalidRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * @ClassName UnifiedExceptionAdvice
 * @author qiezhichao
 * @date 2022/11/1 13:08
 */
@RestControllerAdvice
public class UnifiedExceptionAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnifiedExceptionAdvice.class);

    @ExceptionHandler(BusinessRuntimeException.class)
    @ResponseBody
    public ResponseResult<?> handle(BusinessRuntimeException e) {
        LOGGER.error(e.getMessage(), e);
        return ResponseResult.fail(e.getMessage(), e.getData());
    }

    @ExceptionHandler(InvalidRuntimeException.class)
    @ResponseBody
    public ResponseResult<?> handle(InvalidRuntimeException e) {
        LOGGER.error(e.getMessage(), e);
        return ResponseResult.fail(e.getMessage(), e.getData());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseResult<?> handle(IllegalArgumentException e) {
        LOGGER.error(e.getMessage(), e);
        return ResponseResult.fail(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseResult<?> handle(MethodArgumentNotValidException e) {
        LOGGER.error(e.getMessage(), e);
        String errorMsg = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(" & "));
        return ResponseResult.fail(errorMsg);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult<?> handle(Exception e) {
        LOGGER.error(e.getMessage(), e);
        return ResponseResult.fail("系统繁忙，请稍后重试");
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult<?> handle(Throwable e) {
        LOGGER.error(e.getMessage(), e);
        return ResponseResult.fail("系统繁忙，请稍后重试");
    }
}
