package ink.testpulse.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ink.testpulse.common.Result;
import ink.testpulse.common.ResultCode;
import ink.testpulse.entity.Project;
import ink.testpulse.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目管理 控制器
 */
@RestController
@RequestMapping("/api/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    /**
     * 创建项目
     */
    @PostMapping("/create")
    public Result<String> create(@RequestBody Project project) {
        boolean success = projectService.createProject(project);
        return success ? Result.success("创建项目成功") : Result.error(ResultCode.ERROR);
    }

    /**
     * 获取单个项目详情
     */
    @GetMapping("/{id}")
    public Result<Project> getDetail(@PathVariable Long id) {
        Project project = projectService.getById(id);
        if (project == null) {
            // 如果查不到，也可以利用枚举返回特定错误
            return Result.error(ResultCode.PROJECT_NOT_FOUND);
        }
        return Result.success(project);
    }

    /**
     * 分页查询项目列表
     * @param current 当前页码
     * @param size 每页条数
     */
    @GetMapping("/list")
    public Result<Page<Project>> list(@RequestParam(defaultValue = "1") Integer current,
                                      @RequestParam(defaultValue = "10") Integer size) {
        Page<Project> page = new Page<>(current, size);
        // 这里返回 Page 对象，Result.success 会自动处理泛型
        return Result.success(projectService.page(page));
    }

    /**
     * 更新项目信息
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody Project project) {
        // 调用 Service 层定义的更新逻辑，确保包含业务校验
        boolean success = projectService.updateProject(project);
        return success ? Result.success("项目信息更新成功") : Result.error(ResultCode.ERROR);
    }

    /**
     * 逻辑删除项目
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = projectService.removeById(id);
        return success ? Result.success("项目已成功删除") : Result.error(ResultCode.ERROR);
    }
}