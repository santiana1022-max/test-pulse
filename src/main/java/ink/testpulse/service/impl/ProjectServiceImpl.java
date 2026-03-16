package ink.testpulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ink.testpulse.common.BusinessException;
import ink.testpulse.common.ResultCode;
import ink.testpulse.dto.ProjectResponse;
import ink.testpulse.dto.ProjectSaveRequest;
import ink.testpulse.entity.Project;
import ink.testpulse.mapper.ProjectMapper;
import ink.testpulse.service.ProjectService;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目管理业务实现类
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    @Override
    public ProjectResponse createProject(ProjectSaveRequest request) {
        // 1. 核心业务校验：检查标识符 (identifier) 是否已存在
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Project::getIdentifier, request.getIdentifier());

        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.PROJECT_IDENTIFIER_EXISTS);
        }

        // 2. 初始化统计字段（虽然数据库有默认值，但代码中显式设为 0 更安全）
        Project project = Project.builder()
                .name(request.getName())
                .identifier(request.getIdentifier())
                .description(request.getDescription())
                .owner(request.getOwner())
                .interfaceCount(0)
                .caseCount(0)
                .build();

        // 3. 执行插入
        this.save(project);

        // 4. 组装并返回给前端的 Response DTO
        ProjectResponse response = new ProjectResponse();
        // 使用 BeanUtils 将 project 中的数据完美拷贝到 response 中
        BeanUtils.copyProperties(project, response);

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 涉及数据变更，建议加上事务
    public void updateProject(ProjectSaveRequest request) {
        // 1. 检查项目是否存在
        Project existingProject = this.getById(request.getId());
        if (existingProject == null) {
            throw new BusinessException(ResultCode.PROJECT_NOT_FOUND);
        }

        // 2. 唯一性校验：如果修改了 identifier，需确认新标识没被别人占用
        if (!existingProject.getIdentifier().equals(request.getIdentifier())) {
            LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Project::getIdentifier, request.getIdentifier())
                    .ne(Project::getId, request.getId()); // 排除掉自己本身

            if (this.count(queryWrapper) > 0) {
                throw new BusinessException(ResultCode.PROJECT_IDENTIFIER_EXISTS);
            }
        }

        // 3. 属性拷贝：将 DTO 中允许修改的字段同步到实体类中
        // 这样可以确保 interfaceCount, caseCount, createTime 等字段不会被覆盖
        BeanUtils.copyProperties(request, existingProject);

        // 4. 执行更新
        this.updateById(existingProject);
    }

    @Override
    public ProjectResponse getProjectDetail(Long id) {
        // 1. 获取底层实体
        Project project = this.getById(id);

        // 2. 判空拦截：如果找不到，直接抛出业务异常交由全局异常处理器接管
        if (project == null) {
            throw new BusinessException( "该项目不存在或已被删除");
        }

        // 3. 剥离并转换 DTO
        ProjectResponse response = new ProjectResponse();
        BeanUtils.copyProperties(project, response);

        return response;
    }

    @Override
    public Page<ProjectResponse> getProjectPage(Integer current, Integer size) {
        Page<Project> page = new Page<>(current, size);
        Page<Project> projectPage = this.page(page);

        Page<ProjectResponse> responsePage = new Page<>(projectPage.getCurrent(), projectPage.getSize(), projectPage.getTotal());

        List<ProjectResponse> responseList = projectPage.getRecords().stream().map(project -> {
            ProjectResponse response = new ProjectResponse();
            BeanUtils.copyProperties(project, response);
            return response;
        }).collect(Collectors.toList());

        responsePage.setRecords(responseList);
        return responsePage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProject(Long id) {
        // 1. 检查项目是否存在
        Project project = this.getById(id);
        if (project == null) {
            throw new BusinessException(ResultCode.PROJECT_NOT_FOUND);
        }

        // 2. 级联逻辑预留 (重要！)
        // 未来这里需要调用 moduleService.deleteByProjectId(id);
        // 确保项目删掉后，旗下的模块、接口、用例也一并标记为逻辑删除

        // 3. 执行删除 (触发 MyBatis-Plus 的逻辑删除)
        boolean success = this.removeById(id);
        if (!success) {
            throw new BusinessException("项目删除失败");
        }
    }
}