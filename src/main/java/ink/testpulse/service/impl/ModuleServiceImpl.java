package ink.testpulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ink.testpulse.common.BusinessException;
import ink.testpulse.common.ResultCode;
import ink.testpulse.dto.ModuleSaveRequest;
import ink.testpulse.dto.ModuleTreeResponse;
import ink.testpulse.entity.Module;
import ink.testpulse.mapper.ModuleMapper;
import ink.testpulse.service.ModuleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleServiceImpl extends ServiceImpl<ModuleMapper, Module> implements ModuleService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveModule(ModuleSaveRequest request) {
        Module module = new Module();
        BeanUtils.copyProperties(request, module);

        // 1. 处理层级逻辑
        if (request.getParentId() == null || request.getParentId() == 0) {
            module.setParentId(0L);
            module.setLevel(1);
        } else {
            Module parent = this.getById(request.getParentId());
            if (parent == null) {
                throw new BusinessException(ResultCode.MODULE_PARENT_NOT_FOUND);
            }
            int newLevel = parent.getLevel() + 1;
            // 维持你的 3 层深度限制
            if (newLevel > 3) {
                throw new BusinessException(ResultCode.MODULE_DEPTH_EXCEED);
            }
            module.setLevel(newLevel);
        }

        // 2. 初始化/更新统计数据 (如果是新增)
        if (module.getId() == null) {
            module.setInterfaceCount(0);
            module.setCaseCount(0);
        }

        this.saveOrUpdate(module);
    }

    @Override
    public ModuleTreeResponse getModuleDetail(Long id) {
        Module module = this.getById(id);
        if (module == null) {
            throw new BusinessException(ResultCode.MODULE_PARENT_NOT_FOUND);
        }
        ModuleTreeResponse response = new ModuleTreeResponse();
        BeanUtils.copyProperties(module, response);
        return response;
    }

    @Override
    public List<ModuleTreeResponse> getModuleTree(Long projectId) {
        // 1. 获取该项目下所有模块
        List<Module> allModules = this.list(new LambdaQueryWrapper<Module>()
                .eq(Module::getProjectId, projectId)
                .orderByAsc(Module::getId));

        if (allModules.isEmpty()) return new ArrayList<>();

        // 2. 将所有节点转为 DTO 并存入 Map
        Map<Long, ModuleTreeResponse> nodeMap = new LinkedHashMap<>();
        for (Module m : allModules) {
            ModuleTreeResponse node = new ModuleTreeResponse();
            BeanUtils.copyProperties(m, node);
            node.setChildren(new ArrayList<>()); // 必须初始化，防止前端报错
            nodeMap.put(node.getId(), node);
        }

        // 3. 循环遍历 Map，将子节点挂载到父节点下 (非递归 $O(n)$ 算法)
        List<ModuleTreeResponse> rootNodes = new ArrayList<>();
        for (ModuleTreeResponse node : nodeMap.values()) {
            Long parentId = node.getParentId();
            if (parentId == null || parentId == 0) {
                rootNodes.add(node);
            } else {
                ModuleTreeResponse parentNode = nodeMap.get(parentId);
                if (parentNode != null) {
                    parentNode.getChildren().add(node);
                } else {
                    // 孤儿节点作为顶级节点处理
                    rootNodes.add(node);
                }
            }
        }
        return rootNodes;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteModule(Long id) {
        Module module = this.getById(id);
        if (module == null) throw new BusinessException(ResultCode.MODULE_PARENT_NOT_FOUND);

        // 1. 校验是否有子模块
        long childCount = this.count(new LambdaQueryWrapper<Module>().eq(Module::getParentId, id));
        if (childCount > 0) throw new BusinessException(ResultCode.MODULE_HAS_CHILDREN);

        // 2. 校验是否有关联数据
        if (module.getInterfaceCount() > 0 || module.getCaseCount() > 0) {
            throw new BusinessException(ResultCode.MODULE_HAS_DATA);
        }

        this.removeById(id);
    }
}