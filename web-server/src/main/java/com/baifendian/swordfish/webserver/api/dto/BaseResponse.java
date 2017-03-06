/*
 * Create Author  : dsfan
 * Create Date    : 2016-7-26
 * File Name      : BaseResponse.java
 */

package com.baifendian.swordfish.webserver.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Arrays;

/**
 * 返回包基类
 * <p>
 * 
 * @author : dsfan
 * @date : 2016-7-26
 */

public class BaseResponse {
    /** 返回状态码 */
    private int status;

    /** 编码 */
    @JsonIgnore
    private String code;

    /** 错误信息，当有错误的情况下返回 */
    private String msg;

    /** 错误信息的参数 */
    @JsonIgnore
    private Object[] args;

    /** {@link BaseData} */
    private BaseData data;

    /** session 不存在或过期 */
    public static final BaseResponse SESSION_NOT_EXIST = new BaseResponse(1, "com.bfd.dw.api.dto.BaseResponse.sessionExpired", null);

    /** session 来源不正确（浏览器或者 machine 违法） */
    public static final BaseResponse SESSION_SOURCE_NOT_SUPPORT = new BaseResponse(2, "com.bfd.dw.api.dto.BaseResponse.sessionSourceInvalid", null);

    /** session 权限校验失败，如用户没有执行某任务权限 */
    public static final BaseResponse SESSION_PERMISSION_DENIED = new BaseResponse(3, "com.bfd.dw.api.dto.BaseResponse.sessionPermCheckFailed", null);

    /** 服务内部错误，指服务本身发生了异常 */
    public static final BaseResponse INTERNAL_ERROR = new BaseResponse(4, "com.bfd.dw.api.dto.BaseResponse.internalException", null);

    /** 其它错误，需要查看 error 信息，指具体模块的异常 */
    public static final BaseResponse OTHER_ERROR = new BaseResponse(5, "com.bfd.dw.api.dto.BaseResponse.otherException", null);

    /** 在查询接口中，查询为空的情况 */
    public static final BaseResponse NO_DATA_ERROR = new BaseResponse(6, "com.bfd.dw.api.dto.BaseResponse.queryResultIsEmpty", null);

    /** 在 adhoc 查询执行结果的接口中，如果需要等待执行完成，则返回该状态码 */
    public static final BaseResponse WAIT_QUERY_RESULT = new BaseResponse(7, "com.bfd.dw.api.dto.BaseResponse.waitingForResult", null);

    /**
     * constructor
     */
    public BaseResponse() {
    }

    /**
     * @param status
     * @param code
     * @param args
     */
    public BaseResponse(int status, String code, Object[] args) {
        super();
        this.status = status;
        this.code = code;
        this.args = args;
    }

    /**
     * @param status
     * @param code
     * @param args
     * @param data
     */
    public BaseResponse(int status, String code, Object[] args, BaseData data) {
        super();
        this.status = status;
        this.code = code;
        this.args = args;
        this.data = data;
    }

    /**
     * 创建一个其他类型异常的返回包
     * <p>
     *
     * @param code
     * @param args
     * @return {@link BaseResponse}
     */
    public static BaseResponse createOtherErrorResponse(String code, Object...args) {
        return new BaseResponse(OTHER_ERROR.getStatus(), code, args);
    }

    /**
     * 创建一个成功的返回包
     * <p>
     *
     * @param data
     * @return {@link BaseResponse}
     */
    public static BaseResponse createSuccessResponse(BaseData data) {
        return new BaseResponse(0, null, null, data);
    }

    /**
     * 创建一个成功的返回包
     * <p>
     *
     * @param code
     * @param data
     * @parm args
     * @return {@link BaseResponse}
     */
    public static BaseResponse createSuccessResponse(String code, BaseData data, Object...args) {
        return new BaseResponse(0, code, args, data);
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
            "status=" + status +
            ", code='" + code + '\'' +
            ", msg='" + msg + '\'' +
            ", args=" + Arrays.toString(args) +
            ", data=" + data +
            '}';
    }

    /**
     * getter method
     * 
     * @see BaseResponse#status
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * setter method
     * 
     * @see BaseResponse#status
     * @param status
     *            the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * getter method
     * 
     * @see BaseResponse#code
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * setter method
     * 
     * @see BaseResponse#code
     * @param code
     *            the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * getter method
     *
     * @see BaseResponse#msg
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * setter method
     *
     * @see BaseResponse#msg
     * @param msg
     *            the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * getter method
     * 
     * @see BaseResponse#data
     * @return the data
     */
    public BaseData getData() {
        return data;
    }

    /**
     * setter method
     * 
     * @see BaseResponse#data
     * @param data
     *            the data to set
     */
    public void setData(BaseData data) {
        this.data = data;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
