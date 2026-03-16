package ink.testpulse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ink.testpulse.dto.TestCaseDetailResponse;
import ink.testpulse.dto.TestCaseSaveRequest;
import ink.testpulse.entity.TestCase;

public interface TestCaseService extends IService<TestCase> {

    /**
     * 保存完整的测试用例 (主表 + 步骤子表)
     * @param request 复合请求体
     * @return 生成的主用例 ID
     */
    Long saveTestCaseWithSteps(TestCaseSaveRequest request);

    /**
     * 获取测试用例详情 (包含步骤及接口基础信息)
     * @param id 用例主键ID
     * @return 组装好的用例详情
     */
    TestCaseDetailResponse getDetail(Long id);
}