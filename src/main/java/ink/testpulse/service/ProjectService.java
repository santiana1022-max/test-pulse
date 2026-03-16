package ink.testpulse.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ink.testpulse.dto.ProjectResponse;
import ink.testpulse.dto.ProjectSaveRequest;
import ink.testpulse.entity.Project;

/**
 * 项目管理业务接口
 */
public interface ProjectService extends IService<Project> {

    /**
     * 创建新项目（包含唯一性校验）
     * @param request 项目实体
     * @return 是否成功
     */
    ProjectResponse createProject(ProjectSaveRequest request);

    void updateProject(ProjectSaveRequest request);

    ProjectResponse getProjectDetail(Long id);

    Page<ProjectResponse> getProjectPage(Integer current, Integer size);

    void deleteProject(Long id);
}