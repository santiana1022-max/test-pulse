package ink.testpulse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ink.testpulse.entity.Module;
import ink.testpulse.entity.vo.ModuleVO;

import java.util.List;

/**
 * 模块服务接口
 */
public interface ModuleService extends IService<Module> {

    /**
     * 创建模块 (包含层级校验)
     */
    boolean createModule(Module module);

    /**
     * 获取项目的模块树
     * @param projectId 项目ID
     * @return 树形结构的模块列表
     */
    List<ModuleVO> getModuleTree(Long projectId);

    /**
     * 安全删除模块 (包含子节点和关联数据校验)
     */
    boolean deleteModule(Long id);
}