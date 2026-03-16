package ink.testpulse.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 测试用例保存请求体 (包含主表信息和步骤列表)
 */
@Data
public class TestCaseSaveRequest {

    // --- 主表信息 ---

    @NotNull(message = "所属项目ID不能为空")
    private Long projectId;

    private Long moduleId;

    @NotBlank(message = "用例名称不能为空")
    private String name;

    private String description;

    private String priority;

    // --- 步骤列表 ---

    @NotEmpty(message = "测试用例至少需要包含一个执行步骤")
    @Valid // 激活内部元素的嵌套校验
    private List<StepDTO> steps;

    /**
     * 步骤 DTO (内部类)
     */
    @Data
    public static class StepDTO {
        private Long interfaceId;

        @NotNull(message = "步骤顺序不能为空")
        private Integer stepOrder;

        @NotBlank(message = "步骤名称不能为空")
        private String name;

        // 步骤专属参数和规则
        private Object requestHeaders;
        private Object requestParams;
        private String requestBodyType;
        private String requestBody;

        private Object assertRules;
        private Object extractRules;
    }
}