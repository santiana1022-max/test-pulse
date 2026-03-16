package ink.testpulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ink.testpulse.dto.EnvironmentSaveRequest;
import ink.testpulse.entity.Environment;
import ink.testpulse.mapper.EnvironmentMapper;
import ink.testpulse.service.EnvironmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnvironmentServiceImpl extends ServiceImpl<EnvironmentMapper, Environment> implements EnvironmentService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveEnvironment(EnvironmentSaveRequest request) {
        Environment environment = new Environment();
        BeanUtils.copyProperties(request, environment);

        // 使用 MyBatis-Plus 内置的 saveOrUpdate
        this.saveOrUpdate(environment);

        // 自动回填的 ID 返回给前端
        return environment.getId();
    }

    @Override
    public List<Environment> listByProjectId(Long projectId) {
        return this.list(new LambdaQueryWrapper<Environment>()
                .eq(Environment::getProjectId, projectId)
                .orderByDesc(Environment::getCreateTime)
        );
    }
}