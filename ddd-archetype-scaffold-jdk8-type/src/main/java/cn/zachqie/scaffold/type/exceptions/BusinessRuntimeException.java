package cn.zachqie.scaffold.type.exceptions;

import lombok.Getter;

/**
 * @ClassName BusinessRuntimeException
 * @Author qiezhichao
 * @Date 2022/10/27 10:13
 */
@Getter
public class BusinessRuntimeException extends RuntimeException{

    private Object data;

    public BusinessRuntimeException(String message) {
        super(message);
    }

    public BusinessRuntimeException(Object data, String message) {
        super(message);
        this.data = data;
    }

    public BusinessRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessRuntimeException(Throwable cause) {
        super(cause);
    }
}
