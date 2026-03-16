package ink.testpulse.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 项目基础信息实体
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tp_project")
public class Project {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 项目名称 */
    private String name;

    /** 项目唯一标识(如: ERP, CRM) */
    private String identifier;

    /** 项目描述 */
    private String description;

    /** 项目负责人(暂时存字符串) */
    private String owner;

    /** 接口总数 */
    private Integer interfaceCount;

    /** 用例总数 */
    private Integer caseCount;

    /** 最近一次执行时间 */
    private LocalDateTime lastExecuteTime;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除(0:未删, 1:已删) */
    @TableLogic
    private Integer deleted;
}