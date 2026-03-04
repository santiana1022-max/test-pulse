package ink.testpulse.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 模块实体类
 */
@Data
@TableName("sys_module")
public class Module {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属项目ID
     */
    private Long projectId;

    /**
     * 父模块ID (顶级模块为0)
     */
    private Long parentId;

    /**
     * 模块名称
     */
    private String name;

    /**
     * 模块描述
     */
    private String description;

    /**
     * 层级深度 (用于限制前端展示，如不能超过3层)
     */
    private Integer level;

    /**
     * 接口总数
     */
    private Integer interfaceCount;

    /**
     * 用例总数
     */
    private Integer caseCount;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 创建时间 (MyBatis-Plus自动填充)
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间 (MyBatis-Plus自动填充)
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标识
     */
    @TableLogic
    private Integer deleted;
}