package ink.testpulse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ink.testpulse.dto.TestCaseSaveRequest;
import ink.testpulse.entity.TestCase;
import ink.testpulse.entity.TestCaseStep;
import ink.testpulse.mapper.TestCaseMapper;
import ink.testpulse.service.TestCaseService;
import ink.testpulse.service.TestCaseStepService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestCaseServiceImpl extends ServiceImpl<TestCaseMapper, TestCase> implements TestCaseService {

    @Autowired
    private TestCaseStepService testCaseStepService;

    @Override
    // 核心点：开启事务，遇到任何异常全部回滚，防止出现“主表建了，步骤没进去”的脏数据
    @Transactional(rollbackFor = Exception.class)
    public Long saveTestCaseWithSteps(TestCaseSaveRequest request) {

        // 1. 组装并保存主表
        TestCase testCase = new TestCase();
        BeanUtils.copyProperties(request, testCase);
        // 如果没传优先级，默认给个 P2
        if (testCase.getPriority() == null) {
            testCase.setPriority("P2");
        }

        // 执行插入主表，MyBatis-Plus 会自动将生成的 ID 填回 testCase 对象中
        this.save(testCase);
        Long caseId = testCase.getId();

        // 2. 组装并保存步骤子表
        List<TestCaseStep> stepList = new ArrayList<>();
        for (TestCaseSaveRequest.StepDTO stepDTO : request.getSteps()) {
            TestCaseStep step = new TestCaseStep();
            BeanUtils.copyProperties(stepDTO, step);

            // 关键：将主表生成的 ID 塞给每一个子步骤，建立主从绑定关系
            step.setCaseId(caseId);

            stepList.add(step);
        }

        // 执行批量插入步骤表，极大提升数据库性能
        testCaseStepService.saveBatch(stepList);

        // 3. 返回生成的主键 ID，方便前端后续做跳转或高亮
        return caseId;
    }
}