package cn.wizzer.framework.base;

/**
 * 业务提示信息处理异常类
 */
public class ValidatException extends RuntimeException {

    public ValidatException(String msg)
    {
        super(msg);
    }

}
