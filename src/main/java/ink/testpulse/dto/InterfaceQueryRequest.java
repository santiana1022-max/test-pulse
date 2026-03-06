package ink.testpulse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 接口分页查询请求体
 */
@Data
public class InterfaceQueryRequest {

    /**
     * 当前页码 (默认 1)
     */
    private Integer current = 1;

    /**
     * 每页条数 (默认 10)
     */
    private Integer size = 10;

    /**
     * 所属项目ID (必传，用于隔离不同项目的数据)
     */
    @NotNull(message = "所属项目ID不能为空")
    private Long projectId;

    /**
     * 所属模块ID (选传，精确匹配特定模块下的接口)
     */
    private Long moduleId;

    /**
     * 接口名称 (选传，支持模糊搜索)
     */
    private String name;

    /**
     * 接口状态 (选传，0-草稿, 1-发布, 2-废弃)
     */
    private Integer status;
}