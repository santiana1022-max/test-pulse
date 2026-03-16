package ink.testpulse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ink.testpulse.dto.TestCaseSaveRequest;
import ink.testpulse.entity.TestCase;

public interface TestCaseService extends IService<TestCase> {

    /**
     * 保存完整的测试用例 (主表 + 步骤子表)
     * @param request 复合请求体
     * @return 生成的主用例 ID
     */
    Long saveTestCaseWithSteps(TestCaseSaveRequest request);
}