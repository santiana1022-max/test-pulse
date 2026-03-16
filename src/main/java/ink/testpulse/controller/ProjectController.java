package ink.testpulse.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ink.testpulse.common.Result;
import ink.testpulse.common.ResultCode;
import ink.testpulse.dto.ProjectResponse;
import ink.testpulse.dto.ProjectSaveRequest;
import ink.testpulse.entity.Project;
import ink.testpulse.service.ProjectService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
    public Result<ProjectResponse> create(@Validated @RequestBody ProjectSaveRequest request) {
        // 调用 Service 层，拿到完整的新项目信息
        ProjectResponse response = projectService.createProject(request);

        // 直接返回给前端
        return Result.success(response);
    }

    /**
     * 获取单个项目详情
     */
    @GetMapping("/{id}")
    public Result<ProjectResponse> getDetail(@PathVariable Long id) {
        // Controller 彻底解耦，只负责接收请求和统一包装 Result
        return Result.success(projectService.getProjectDetail(id));
    }

    /**
     * 分页查询项目列表
     * @param current 当前页码
     * @param size 每页条数
     */
    @GetMapping("/list")
    public Result<Page<ProjectResponse>> list(@RequestParam(defaultValue = "1") Integer current,
                                              @RequestParam(defaultValue = "10") Integer size) {
        // 一行代码搞定，Controller 彻底变“瘦”！
        return Result.success(projectService.getProjectPage(current, size));
    }

    /**
     * 更新项目信息
     */
    @PutMapping("/update")
    public Result<String> update(@Validated @RequestBody ProjectSaveRequest request) {
        // 1. 强制校验 ID 不能为空（更新的基础）
        if (request.getId() == null) {
            return Result.error(ResultCode.PARAM_IS_INVALID);
        }

        // 2. 调用下沉到 Service 的业务逻辑
        projectService.updateProject(request);

        return Result.success("项目信息更新成功");
    }

    /**
     * 逻辑删除项目
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        // 逻辑下沉，Service 内部可以处理复杂的级联删除逻辑
        projectService.deleteProject(id);
        return Result.success("项目已成功删除");
    }
}