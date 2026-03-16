package ink.testpulse.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 测试用例步骤表 (具体执行单元)
 */
@Data
// 核心：必须开启 autoResultMap = true，否则查询时无法将 JSON 转回 Object
@TableName(value = "tp_test_case_step", autoResultMap = true)
public class TestCaseStep {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long caseId;

    private Long interfaceId;

    private Integer stepOrder;

    private String name;

    // --- 以下字段使用 JacksonTypeHandler 自动处理 JSON 转换 ---

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object requestHeaders;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object requestParams;

    private String requestBodyType;

    private String requestBody;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object assertRules;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object extractRules;

    // --------------------------------------------------------

    private String createBy;

    private String updateBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}