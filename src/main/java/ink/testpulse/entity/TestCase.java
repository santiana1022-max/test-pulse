package ink.testpulse.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 测试用例主表 (业务场景容器)
 */
@Data
@TableName("tp_test_case")
public class TestCase {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long projectId;

    private Long moduleId;

    private String name;

    private String description;

    private String priority;

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