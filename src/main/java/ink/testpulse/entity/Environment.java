package ink.testpulse.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目环境管理表 (动态变量池的载体)
 */
@Data
@TableName(value = "tp_environment", autoResultMap = true)
public class Environment {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属项目ID
     */
    private Long projectId;

    /**
     * 环境名称 (如: QA测试环境)
     */
    private String name;

    /**
     * 基础URL前缀 (如: http://api.qa.testpulse.ink)
     */
    private String baseUrl;

    /**
     * 环境变量池 (JSON格式，存储全局静态和动态提取的变量)
     * 使用 JacksonTypeHandler 自动处理 JSON 字符串与 Java Object 的转换
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object variables;

    private String description;

    private Integer status;

    private String createBy;

    private String updateBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}