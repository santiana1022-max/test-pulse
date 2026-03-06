package ink.testpulse.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 接口信息实体类
 */
@Data
@TableName(value = "tp_interface", autoResultMap = true)
public class InterfaceInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属项目ID
     */
    private Long projectId;

    /**
     * 所属模块ID
     */
    private Long moduleId;

    /**
     * 接口名称
     */
    private String name;

    /**
     * 接口路径 (如: /api/v1/login)
     */
    private String path;

    /**
     * 请求方式 (GET/POST/PUT/DELETE等)
     */
    private String method;

    /**
     * 状态 (0-草稿, 1-发布, 2-废弃)
     */
    private Integer status;

    /**
     * 请求头配置 (映射为 JSON 对象/数组)
     * 使用 JacksonTypeHandler 自动在 Java 对象与 JSON 字符串间转换
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object requestHeaders;

    /**
     * URL参数配置 (映射为 JSON 对象/数组)
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object requestParams;

    /**
     * 请求体类型 (none/form-data/json/raw等)
     */
    private String requestBodyType;

    /**
     * 请求体内容
     */
    private String requestBody;

    /**
     * 预期响应体
     */
    private String responseBody;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标识
     */
    @TableLogic
    private Integer deleted;
}