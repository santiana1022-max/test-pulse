package ink.testpulse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ink.testpulse.entity.TestCaseStep;
import ink.testpulse.mapper.TestCaseStepMapper;
import ink.testpulse.service.TestCaseStepService;
import org.springframework.stereotype.Service;

@Service
public class TestCaseStepServiceImpl extends ServiceImpl<TestCaseStepMapper, TestCaseStep> implements TestCaseStepService {
}