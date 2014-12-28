package com.dd.whateat.aibang.open.client.exception;

/**
 * 爱帮服务器返回的响应异常. 对于开放API返回的响应，如果statusCode不是200，则会认为是异常，
 * 该异常会保存响应的statusCode和消息体内容.
 */
public class AibangServerException extends AibangException {

    /**
     * 对于非200的响应，使用statusCode和响应消息体构造.
     * @param statusCode 响应的statusCode
     * @param errMsg 响应的消息体
     */
    public AibangServerException(int statusCode, String errMsg) {
        super(errMsg);
        this.statusCode = statusCode;
    }
    
    /**
     * 获取异常的statusCode。
     * @return statusCode, 具体含义请参考网站文档.
     */
    public int getStatusCode() {
        return statusCode;
    }
    
    private int statusCode;
    
    private static final long serialVersionUID = 1L;
}
