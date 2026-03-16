package ink.testpulse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;
import lombok.Data;

/**
 * 环境保存请求体
 */
@Data
public class EnvironmentSaveRequest {

    /**
     * 环境ID (如果不传则为新增，传了则为修改)
     */
    private Long id;

    @NotNull(message = "所属项目ID不能为空")
    private Long projectId;

    @NotBlank(message = "环境名称不能为空")
    private String name;

    @NotBlank(message = "基础URL前缀不能为空")
    @URL(message = "基础URL格式不正确，必须包含http://或https://")
    private String baseUrl;

    /**
     * 环境变量池 (建议前端传 JSON 对象)
     */
    private Object variables;

    private String description;
}