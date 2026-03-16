package ink.testpulse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ink.testpulse.dto.EnvironmentSaveRequest;
import ink.testpulse.entity.Environment;

import java.util.List;

public interface EnvironmentService extends IService<Environment> {

    /**
     * 保存或更新环境
     * @return 返回环境ID
     */
    Long saveEnvironment(EnvironmentSaveRequest request);

    /**
     * 根据项目ID获取环境列表
     */
    List<Environment> listByProjectId(Long projectId);
}