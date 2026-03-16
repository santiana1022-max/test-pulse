package ink.testpulse.service;

/**
 * 测试用例核心执行引擎
 */
public interface TestCaseRunnerService {

    /**
     * 运行指定的测试用例
     * @param caseId 测试用例主表ID
     * @param environmentId 运行环境ID
     * @return 运行是否全部成功
     */
    boolean runTestCase(Long caseId, Long environmentId);
}