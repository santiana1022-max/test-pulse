package ink.testpulse.entity.vo;

import ink.testpulse.entity.Module;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 模块视图对象 (用于前端展示树形结构)
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ModuleVO extends Module {

    /**
     * 子模块列表
     */
    private List<ModuleVO> children;
}