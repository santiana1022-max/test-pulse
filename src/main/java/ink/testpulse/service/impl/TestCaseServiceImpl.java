package ink.testpulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ink.testpulse.common.BusinessException;
import ink.testpulse.common.ResultCode;
import ink.testpulse.dto.TestCaseDetailResponse;
import ink.testpulse.dto.TestCaseSaveRequest;
import ink.testpulse.entity.InterfaceInfo;
import ink.testpulse.entity.TestCase;
import ink.testpulse.entity.TestCaseStep;
import ink.testpulse.mapper.TestCaseMapper;
import ink.testpulse.service.InterfaceInfoService;
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

    @Autowired
    private InterfaceInfoService interfaceInfoService;

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

    @Override
    public TestCaseDetailResponse getDetail(Long id) {
        // 1. 查询用例主表
        TestCase testCase = this.getById(id);
        if (testCase == null) {
            throw new BusinessException( "该测试用例不存在");
        }

        // 2. 拷贝主表基础信息到 DTO
        TestCaseDetailResponse response = new TestCaseDetailResponse();
        BeanUtils.copyProperties(testCase, response);

        // 3. 查询关联的步骤子表 (严格按照 step_order 升序排列)
        List<TestCaseStep> steps = testCaseStepService.list(
                new LambdaQueryWrapper<TestCaseStep>()
                        .eq(TestCaseStep::getCaseId, id)
                        .orderByAsc(TestCaseStep::getStepOrder)
        );

        // 4. 遍历步骤，反查接口基础信息并组装
        List<TestCaseDetailResponse.StepDetailDTO> stepDTOs = new ArrayList<>();
        for (TestCaseStep step : steps) {
            TestCaseDetailResponse.StepDetailDTO stepDTO = new TestCaseDetailResponse.StepDetailDTO();
            BeanUtils.copyProperties(step, stepDTO);

            // 智能联动：根据 interfaceId 去查真实的 Path 和 Method
            if (step.getInterfaceId() != null) {
                InterfaceInfo interfaceInfo = interfaceInfoService.getById(step.getInterfaceId());
                if (interfaceInfo != null) {
                    stepDTO.setInterfacePath(interfaceInfo.getPath());
                    stepDTO.setInterfaceMethod(interfaceInfo.getMethod());
                }
            }
            stepDTOs.add(stepDTO);
        }

        // 5. 将拼装好的步骤列表塞入响应体返回
        response.setSteps(stepDTOs);
        return response;
    }
}