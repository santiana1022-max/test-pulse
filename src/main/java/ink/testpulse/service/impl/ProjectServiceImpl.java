package ink.testpulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ink.testpulse.common.BusinessException;
import ink.testpulse.common.ResultCode;
import ink.testpulse.entity.Project;
import ink.testpulse.mapper.ProjectMapper;
import ink.testpulse.service.ProjectService;
import org.springframework.stereotype.Service;

/**
 * 项目管理业务实现类
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    @Override
    public boolean createProject(Project project) {
        // 1. 核心业务校验：检查标识符 (identifier) 是否已存在
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Project::getIdentifier, project.getIdentifier());

        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.PROJECT_IDENTIFIER_EXISTS);
        }

        // 2. 初始化统计字段（虽然数据库有默认值，但代码中显式设为 0 更安全）
        project.setInterfaceCount(0);
        project.setCaseCount(0);

        // 3. 执行插入
        return this.save(project);
    }

    @Override
    public boolean updateProject(Project project) {
        // 1. 检查标识符是否冲突
        // 逻辑：查询 identifier 等于当前输入，且 ID 不等于当前项目 ID 的记录
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Project::getIdentifier, project.getIdentifier())
                .ne(Project::getId, project.getId()); // 排除掉自己

        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.PROJECT_IDENTIFIER_EXISTS);
        }

        // 2. 执行更新
        return this.updateById(project);
    }
}