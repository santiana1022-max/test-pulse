package ink.testpulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ink.testpulse.common.BusinessException;
import ink.testpulse.common.ResultCode;
import ink.testpulse.entity.Module;
import ink.testpulse.entity.vo.ModuleVO;
import ink.testpulse.mapper.ModuleMapper;
import ink.testpulse.service.ModuleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModuleServiceImpl extends ServiceImpl<ModuleMapper, Module> implements ModuleService {

    @Override
    public boolean createModule(Module module) {
        // 1. 处理层级 (Level) 逻辑
        if (module.getParentId() == null || module.getParentId() == 0) {
            // 顶级模块
            module.setParentId(0L);
            module.setLevel(1);
        } else {
            // 子模块：查询父模块深度
            Module parent = this.getById(module.getParentId());
            if (parent == null) {
                throw new BusinessException(ResultCode.MODULE_PARENT_NOT_FOUND);
            }
            int newLevel = parent.getLevel() + 1;
            // 严格限制不能超过 3 层
            if (newLevel > 3) {
                throw new BusinessException(ResultCode.MODULE_DEPTH_EXCEED);
            }
            module.setLevel(newLevel);
        }

        // 2. 初始化统计数据
        module.setInterfaceCount(0);
        module.setCaseCount(0);

        return this.save(module);
    }

    @Override
    public List<ModuleVO> getModuleTree(Long projectId) {
        // 1. 一次性查出该项目下的所有模块 (避免在循环中查数据库)
        LambdaQueryWrapper<Module> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Module::getProjectId, projectId)
                .orderByAsc(Module::getId); // 默认按创建顺序排

        List<Module> allModules = this.list(wrapper);

        // 2. 将实体类转换为 VO
        List<ModuleVO> voList = allModules.stream().map(m -> {
            ModuleVO vo = new ModuleVO();
            BeanUtils.copyProperties(m, vo);
            return vo;
        }).collect(Collectors.toList());

        // 3. 递归构建树形结构 (从顶级节点 parentId = 0 开始)
        return buildTree(voList, 0L);
    }

    /**
     * 递归组装树的辅助方法
     */
    private List<ModuleVO> buildTree(List<ModuleVO> list, Long parentId) {
        return list.stream()
                // 找到当前父节点的所有直接子节点
                .filter(node -> node.getParentId().equals(parentId))
                // 递归为其设置子节点
                .peek(node -> node.setChildren(buildTree(list, node.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteModule(Long id) {
        Module module = this.getById(id);
        if (module == null) {
            return false;
        }

        // 1. 强校验：是否有子模块？
        LambdaQueryWrapper<Module> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(Module::getParentId, id);
        if (this.count(childWrapper) > 0) {
            throw new BusinessException(ResultCode.MODULE_HAS_CHILDREN);
        }

        // 2. 强校验：是否有关联接口或用例？
        if (module.getInterfaceCount() > 0 || module.getCaseCount() > 0) {
            throw new BusinessException(ResultCode.MODULE_HAS_DATA);
        }

        // 3. 通过校验，执行逻辑删除
        return this.removeById(id);
    }
}