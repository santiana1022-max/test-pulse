package ink.testpulse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ink.testpulse.entity.InterfaceInfo;

/**
 * 接口信息服务接口
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    /**
     * 保存或新增接口信息 (包含项目与模块的关联一致性校验)
     * @param interfaceInfo 接口实体
     * @return 是否成功
     */
    boolean saveInterfaceInfo(InterfaceInfo interfaceInfo);
}