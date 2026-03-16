package ink.testpulse.dto;

import lombok.Data;

import java.util.List;

/**
 * 模块树形结构响应体 (扁平转嵌套)
 */
@Data
public class ModuleTreeResponse {

    private Long id;

    private Long projectId;

    private Long parentId;

    private String name;

    private String description;

    private Integer level;

    private Integer interfaceCount;

    private Integer caseCount;

    /**
     * 核心树形挂载点：子模块列表
     */
    private List<ModuleTreeResponse> children;
}