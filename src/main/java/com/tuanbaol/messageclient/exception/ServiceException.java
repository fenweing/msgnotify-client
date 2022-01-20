package com.tuanbaol.messageclient.exception;


import com.tuanbaol.messageclient.util.StringUtil;

/**
 * @version 1.0
 * @description 服务器异常类
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private String message;

    public ServiceException(String message) {
        super(message);
        this.message = message;
    }

    public ServiceException(Throwable throwable) {
        super(throwable);
    }

    public ServiceException(Throwable throwable, String template, Object... args) {
        super(throwable);
        this.message = StringUtil.formatByRegex(template, args);
    }

    public ServiceException(String template, Object... args) {
        this.message = StringUtil.formatByRegex(template, args);
    }

    public ServiceException(String message, Throwable throwable) {
        super(message, throwable);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}

