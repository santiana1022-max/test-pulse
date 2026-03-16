package ink.testpulse.dto;

import lombok.Data;

import java.util.List;

/**
 * 测试用例详情响应体 (包含主表及拼装后的步骤列表)
 */
@Data
public class TestCaseDetailResponse {

    private Long id;
    private Long projectId;
    private Long moduleId;
    private String name;
    private String description;
    private String priority;
    private Integer status;

    /**
     * 用例包含的执行步骤列表
     */
    private List<StepDetailDTO> steps;

    @Data
    public static class StepDetailDTO {
        private Long id;
        private Long interfaceId;
        private Integer stepOrder;
        private String name;

        // --- 核心拼装字段：从 InterfaceInfo 反查出来的基础信息 ---
        private String interfacePath;
        private String interfaceMethod;
        // ------------------------------------------------

        // 步骤专属参数和规则
        private Object requestHeaders;
        private Object requestParams;
        private String requestBodyType;
        private String requestBody;

        private Object assertRules;
        private Object extractRules;
    }
}