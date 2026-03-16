package ink.testpulse.service;

import ink.testpulse.dto.InterfaceDebugRequest;
import ink.testpulse.dto.InterfaceDebugResponse;

/**
 * HTTP 请求执行引擎服务
 */
public interface HttpEngineService {

    /**
     * 发起真实的接口调试请求
     * @param request 调试请求参数
     * @return 真实的响应结果
     */
    InterfaceDebugResponse executeRequest(InterfaceDebugRequest request);
}