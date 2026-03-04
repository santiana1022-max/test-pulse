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
    PROJECT_NOT_FOUND(1002, "找不到指定的项目信息"),

    // --- 模块范围 (2000-2999) ---
    MODULE_PARENT_NOT_FOUND(2001, "父模块不存在"),
    MODULE_DEPTH_EXCEED(2002, "模块层级不能超过3层"),
    MODULE_HAS_CHILDREN(2003, "该模块下存在子模块，请先删除子模块"),
    MODULE_HAS_DATA(2004, "该模块下已关联接口或用例，请先清理后删除");

    private final int code;
    private final String msg;
}