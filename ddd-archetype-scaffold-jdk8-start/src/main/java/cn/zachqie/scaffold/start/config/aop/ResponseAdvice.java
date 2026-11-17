package cn.zachqie.scaffold.start.config.aop;


import com.alibaba.fastjson.JSON;
import cn.zachqie.scaffold.type.utils.RequestIdUtils;
import cn.zachqie.scaffold.type.ResponseResult;
import lombok.SneakyThrows;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Objects;
import java.util.UUID;

/**
 * @ClassName RequestIdAdvice
 * @Author qiezhichao
 * @Date 2023/3/7 17:20
 */
@RestControllerAdvice(basePackages = {"cn.zachqie.scaffold.trigger"})
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> clazz) {
        return true;
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object o,
                                  MethodParameter methodParameter,
                                  MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> clazz,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        UUID requestIdUUID = RequestIdUtils.getRequestId();
        String requestId = null;
        if (Objects.nonNull(requestIdUUID)) {
            requestId = requestIdUUID.toString();
        }

        if (o instanceof ResponseResult) {
            ((ResponseResult<?>) o).setRequestId(requestId);
            return o;
        }

        ResponseResult<?> ok = ResponseResult.ok(o);
        ok.setRequestId(requestId);
        return (o instanceof String) ? JSON.toJSONString(ok) : ok;

    }
}
