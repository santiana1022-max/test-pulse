package ink.testpulse.controller;

import ink.testpulse.common.Result;
import ink.testpulse.dto.InterfaceDebugRequest;
import ink.testpulse.dto.InterfaceDebugResponse;
import ink.testpulse.service.HttpEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接口在线调试控制层
 */
@RestController
@RequestMapping("/api/interface")
public class InterfaceDebugController {

    @Autowired
    private HttpEngineService httpEngineService;

    /**
     * 发起真实的接口调试请求
     * 接收前端组装好的报文，通过底层 OkHttp 引擎发送并返回真实结果
     */
    @PostMapping("/debug")
    public Result<InterfaceDebugResponse> debug(@Validated @RequestBody InterfaceDebugRequest request) {
        // 由于我们在 DTO 中加了 @NotBlank，走到这里 URL 和 Method 一定是合法的
        InterfaceDebugResponse response = httpEngineService.executeRequest(request);
        return Result.success(response);
    }
}