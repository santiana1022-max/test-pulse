package ink.testpulse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 测试用例执行请求体
 */
@Data
public class TestCaseRunRequest {

    @NotNull(message = "测试用例ID不能为空")
    private Long caseId;

    @NotNull(message = "运行环境ID不能为空")
    private Long environmentId;
}