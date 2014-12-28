package com.dd.whateat.aibang.open.client.exception;

/**
 * 爱帮开放SDK的所有异常类的基类. 所有该SDK中的异常类都需要从该类继承. 三个构造函数和Exception
 * 类的构造函数是完全一样的.
 */
public class AibangException extends Exception {
    
    public AibangException(String errMsg) {
        super(errMsg);
    }

    public AibangException(String errMsg, Throwable reason) {
        super(errMsg, reason);
    }

    public AibangException(Throwable reason) {
        super(reason);
    }

    private static final long serialVersionUID = 1L;
}
