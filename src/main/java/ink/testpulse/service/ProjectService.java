package ink.testpulse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ink.testpulse.entity.Project;

/**
 * 项目管理业务接口
 */
public interface ProjectService extends IService<Project> {

    /**
     * 创建新项目（包含唯一性校验）
     * @param project 项目实体
     * @return 是否成功
     */
    boolean createProject(Project project);

    boolean updateProject(Project project);
}