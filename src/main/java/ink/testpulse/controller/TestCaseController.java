package ink.testpulse.controller;

import ink.testpulse.common.Result;
import ink.testpulse.dto.TestCaseDetailResponse;
import ink.testpulse.dto.TestCaseSaveRequest;
import ink.testpulse.service.TestCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 测试用例控制层
 */
@RestController
@RequestMapping("/api/testcase")
public class TestCaseController {

    @Autowired
    private TestCaseService testCaseService;

    /**
     * 创建完整的测试用例 (主表 + 步骤列表)
     * 采用主从表事务级联保存
     */
    @PostMapping("")
    public Result<Long> createTestCase(@Validated @RequestBody TestCaseSaveRequest request) {
        // 调用 Service 层带有 @Transactional 事务的保存逻辑
        Long caseId = testCaseService.saveTestCaseWithSteps(request);

        // 返回生成的主键 ID，前端拿到后可以跳转到用例详情页
        return Result.success(caseId);
    }

    /**
     * 获取测试用例详情
     * 会自动级联查询步骤列表，并带出每个步骤对应接口的 Method 和 Path
     */
    @GetMapping("/{id}")
    public Result<TestCaseDetailResponse> getDetail(@PathVariable Long id) {
        TestCaseDetailResponse detail = testCaseService.getDetail(id);
        return Result.success(detail);
    }
}