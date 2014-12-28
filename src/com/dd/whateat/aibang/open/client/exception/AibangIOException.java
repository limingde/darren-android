package com.dd.whateat.aibang.open.client.exception;

import java.io.IOException;

/**
 * 和IOException的作用一样，通常表示网络异常，为了让SDK中的所有异常类都有
 * 统一基类，故而用该类封装了IOException. 
 */
public class AibangIOException extends AibangException {
    
    /**
     * 包装实际的IOException进行构造.
     * @param reason 实际的IOException异常对象
     */
    public AibangIOException(IOException reason) {
        super(reason);
    }
    
    private static final long serialVersionUID = 1L;
}
