package ink.testpulse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 模块保存/更新请求体 (严格限制入参)
 */
@Data
public class ModuleSaveRequest {

    private Long id;

    @NotNull(message = "所属项目ID不能为空")
    private Long projectId;

    @NotNull(message = "父模块ID不能为空，顶级模块请传0")
    private Long parentId;

    @NotBlank(message = "模块名称不能为空")
    private String name;

    private String description;

    // 注意：没有 level 字段！后端在 Service 层拿到 parentId 后，自己去数据库查父级的 level 然后 +1，绝对不相信前端传的 level。
}