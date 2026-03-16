package ink.testpulse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ink.testpulse.dto.ModuleSaveRequest;
import ink.testpulse.dto.ModuleTreeResponse;
import ink.testpulse.entity.Module;

import java.util.List;

public interface ModuleService extends IService<Module> {

    /**
     * 保存或更新模块
     */
    void saveModule(ModuleSaveRequest request);

    /**
     * 获取脱敏后的模块详情
     */
    ModuleTreeResponse getModuleDetail(Long id);

    /**
     * 获取项目的模块树 (非递归算法)
     */
    List<ModuleTreeResponse> getModuleTree(Long projectId);

    /**
     * 安全删除模块
     */
    void deleteModule(Long id);
}