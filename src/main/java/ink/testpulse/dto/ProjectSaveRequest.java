package ink.testpulse.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 项目保存/更新请求体 (严格限制入参)
 */
@Data
public class ProjectSaveRequest {

    /**
     * 项目ID (新增时不传，修改时必传)
     */
    private Long id;

    @NotBlank(message = "项目名称不能为空")
    private String name;

    @NotBlank(message = "项目唯一标识不能为空")
    private String identifier;

    private String description;

    private String owner;
}