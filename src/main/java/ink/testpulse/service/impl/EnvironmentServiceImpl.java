package ink.testpulse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ink.testpulse.entity.Environment;
import ink.testpulse.mapper.EnvironmentMapper;
import ink.testpulse.service.EnvironmentService;
import org.springframework.stereotype.Service;

@Service
public class EnvironmentServiceImpl extends ServiceImpl<EnvironmentMapper, Environment> implements EnvironmentService {
}