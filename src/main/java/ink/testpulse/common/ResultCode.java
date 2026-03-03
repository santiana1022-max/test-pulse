package ink.testpulse.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态码及信息枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // --- 系统通用范围 ---
    SUCCESS(200, "操作成功"),
    ERROR(500, "系统服务繁忙，请稍后再试"),
    VALIDATE_FAILED(400, "参数校验失败"),

    // --- 项目模块范围 (1000-1999) ---
    PROJECT_IDENTIFIER_EXISTS(1001, "该项目标识符已存在，请重新输入"),
    PROJECT_NOT_FOUND(1002, "找不到指定的项目信息");

    private final int code;
    private final String msg;
}