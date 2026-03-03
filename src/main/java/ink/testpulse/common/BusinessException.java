package ink.testpulse.common;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    /**
     * 支持传入枚举
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.code = resultCode.getCode();
    }

    /**
     * 同时也保留支持手动输入文案（灵活应对某些特殊场景）
     */
    public BusinessException(String msg) {
        super(msg);
        this.code = ResultCode.ERROR.getCode();
    }
}