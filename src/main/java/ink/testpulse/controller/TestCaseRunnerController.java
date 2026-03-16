package ink.testpulse.controller;

import ink.testpulse.common.Result;
import ink.testpulse.dto.TestCaseRunRequest;
import ink.testpulse.service.TestCaseRunnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试用例执行引擎控制层
 */
@RestController
@RequestMapping("/api/testcase/runner")
public class TestCaseRunnerController {

    @Autowired
    private TestCaseRunnerService testCaseRunnerService;

    /**
     * 触发执行指定的测试用例链路
     */
    @PostMapping("/run")
    public Result<Boolean> runTestCase(@Validated @RequestBody TestCaseRunRequest request) {
        // 调用底层六步工作流引擎：加载环境 -> 拼装 -> 替换变量 -> 发包 -> 提取 -> 断言
        boolean isAllPassed = testCaseRunnerService.runTestCase(request.getCaseId(), request.getEnvironmentId());

        if (isAllPassed) {
            return Result.success(true, "测试用例链路执行成功，全步骤通过！");
        } else {
            // 这里我们依然返回 200 状态码，但是在 data 里返回 false，表示用例逻辑断言未通过
            return Result.success(false, "测试用例存在断言失败的步骤，请查看后端日志排查！");
        }
    }
}