package ink.testpulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import ink.testpulse.common.BusinessException;
import ink.testpulse.common.ResultCode;
import ink.testpulse.dto.InterfaceDebugRequest;
import ink.testpulse.dto.InterfaceDebugResponse;
import ink.testpulse.entity.Environment;
import ink.testpulse.entity.InterfaceInfo;
import ink.testpulse.entity.TestCase;
import ink.testpulse.entity.TestCaseStep;
import ink.testpulse.service.*;
import ink.testpulse.utils.VariableRegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TestCaseRunnerServiceImpl implements TestCaseRunnerService {

    @Autowired
    private TestCaseService testCaseService;
    @Autowired
    private TestCaseStepService testCaseStepService;
    @Autowired
    private InterfaceInfoService interfaceInfoService;
    @Autowired
    private EnvironmentService environmentService;
    @Autowired
    private HttpEngineService httpEngineService;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean runTestCase(Long caseId, Long environmentId) {
        // 1. 获取测试用例主壳
        TestCase testCase = testCaseService.getById(caseId);
        if (testCase == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED.getCode(), "测试用例不存在");
        }

        // 2. 初始化全局环境与变量池 (Context)
        Environment env = environmentService.getById(environmentId);
        if (env == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED.getCode(), "运行环境不存在");
        }

        // 将数据库里的 JSON 变量池转为 Java Map 大管家
        Map<String, Object> globalVariables = new HashMap<>();
        if (env.getVariables() != null) {
            try {
                String varJson = objectMapper.writeValueAsString(env.getVariables());
                globalVariables = objectMapper.readValue(varJson, new TypeReference<Map<String, Object>>() {});
            } catch (Exception e) {
                log.error("解析环境变量池失败", e);
            }
        }

        // 3. 获取所有执行步骤并按顺序排列
        List<TestCaseStep> steps = testCaseStepService.list(
                new LambdaQueryWrapper<TestCaseStep>()
                        .eq(TestCaseStep::getCaseId, caseId)
                        .orderByAsc(TestCaseStep::getStepOrder)
        );

        boolean allPassed = true;

        // 4. 开始遍历执行每一个步骤
        for (TestCaseStep step : steps) {
            log.info("开始执行用例 [{}], 步骤 [{}]: {}", testCase.getName(), step.getStepOrder(), step.getName());

            // 4.1 获取接口骨架
            InterfaceInfo interfaceInfo = interfaceInfoService.getById(step.getInterfaceId());
            if (interfaceInfo == null) {
                throw new BusinessException(ResultCode.ERROR.getCode(), "步骤关联的接口模板不存在");
            }

            // 4.2 组装请求参数并进行正则变量替换 {{xxx}}
            InterfaceDebugRequest request = new InterfaceDebugRequest();
            request.setEnvironmentId(environmentId);
            // 路径替换：例如 /api/user/{{userId}} 替换为 /api/user/1001
            request.setPath(VariableRegexUtils.replaceVariables(interfaceInfo.getPath(), globalVariables));
            request.setMethod(interfaceInfo.getMethod());

            // 请求头合并与替换 (暂略复杂的深度合并，这里优先使用 Step 特有参数演示逻辑)
            String headersStr = getRenderedJson(step.getRequestHeaders() != null ? step.getRequestHeaders() : interfaceInfo.getRequestHeaders(), globalVariables);
            request.setRequestHeaders(parseToObject(headersStr));

            String paramsStr = getRenderedJson(step.getRequestParams() != null ? step.getRequestParams() : interfaceInfo.getRequestParams(), globalVariables);
            request.setRequestParams(parseToObject(paramsStr));

            request.setRequestBodyType(StringUtils.hasText(step.getRequestBodyType()) ? step.getRequestBodyType() : interfaceInfo.getRequestBodyType());
            // Body 替换：例如 {"token": "{{global_token}}"} 替换为真实的 Token
            request.setRequestBody(VariableRegexUtils.replaceVariables(step.getRequestBody(), globalVariables));

            // 4.3 调用底层引擎发起真实网络请求
            InterfaceDebugResponse response = httpEngineService.executeRequest(request);
            log.info("步骤 [{}] HTTP执行完成, 状态码: {}, 耗时: {}ms", step.getName(), response.getStatusCode(), response.getResponseTime());

            // 4.4 提取变量 (Extract) -> 核心亮点！
            extractVariables(step.getExtractRules(), response.getResponseBody(), globalVariables);

            // 4.5 断言验证 (Assert) -> 决定该步骤是否成功
            boolean stepPassed = doAssert(step.getAssertRules(), response);
            if (!stepPassed) {
                allPassed = false;
                log.warn("步骤 [{}] 断言失败！后续步骤可以选择中断或继续(当前策略为继续)", step.getName());
                // TODO: 可以根据用例配置决定是否直接 break 跳出循环
            }
        }

        // 5. 整个用例执行完毕后，将最新的全局变量池 (包含了新提取的 Token 等) 写回环境数据库！
        try {
            env.setVariables(globalVariables);
            environmentService.updateById(env);
            log.info("环境 [{}] 变量池已更新保存入库", env.getName());
        } catch (Exception e) {
            log.error("更新环境变量池失败", e);
        }

        return allPassed;
    }

    /**
     * 将 Object 转为 JSON 字符串并进行变量替换
     */
    private String getRenderedJson(Object obj, Map<String, Object> variables) {
        if (obj == null) return null;
        try {
            String jsonStr = objectMapper.writeValueAsString(obj);
            return VariableRegexUtils.replaceVariables(jsonStr, variables);
        } catch (Exception e) {
            return null;
        }
    }

    private Object parseToObject(String jsonStr) {
        if (!StringUtils.hasText(jsonStr)) return null;
        try {
            return objectMapper.readValue(jsonStr, Object.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 核心亮点：利用 JsonPath 从响应体中抠出数据，塞入全局变量池
     */
    private void extractVariables(Object extractRulesObj, String responseBody, Map<String, Object> globalVariables) {
        if (extractRulesObj == null || !StringUtils.hasText(responseBody)) return;
        try {
            String rulesJson = objectMapper.writeValueAsString(extractRulesObj);
            List<Map<String, String>> rules = objectMapper.readValue(rulesJson, new TypeReference<List<Map<String, String>>>() {});

            for (Map<String, String> rule : rules) {
                String varName = rule.get("name");
                String type = rule.get("type");
                String expression = rule.get("expression"); // 比如: $.data.token

                if ("jsonPath".equals(type) && StringUtils.hasText(expression)) {
                    // 使用 Jayway JsonPath 提取数据
                    Object extractedValue = JsonPath.read(responseBody, expression);
                    if (extractedValue != null) {
                        globalVariables.put(varName, extractedValue);
                        log.info("成功提取变量: {} = {}", varName, extractedValue);
                    }
                }
            }
        } catch (Exception e) {
            log.error("变量提取执行失败", e);
        }
    }

    /**
     * 核心亮点：断言引擎 (当前实现了基础的状态码和简易 JsonPath 断言)
     */
    private boolean doAssert(Object assertRulesObj, InterfaceDebugResponse response) {
        if (assertRulesObj == null) return true; // 没写断言默认算成功

        try {
            String rulesJson = objectMapper.writeValueAsString(assertRulesObj);
            List<Map<String, String>> rules = objectMapper.readValue(rulesJson, new TypeReference<List<Map<String, String>>>() {});

            for (Map<String, String> rule : rules) {
                String type = rule.get("type");
                String operator = rule.get("operator");
                String expectedValue = rule.get("value");

                // 1. 状态码断言
                if ("statusCode".equals(type)) {
                    if ("equals".equals(operator) && !String.valueOf(response.getStatusCode()).equals(expectedValue)) {
                        log.warn("状态码断言失败: 预期 {}, 实际 {}", expectedValue, response.getStatusCode());
                        return false;
                    }
                }

                // 2. JsonPath 断言
                if ("jsonPath".equals(type)) {
                    String expression = rule.get("expression");
                    Object actualValueObj = JsonPath.read(response.getResponseBody(), expression);
                    String actualValue = String.valueOf(actualValueObj);

                    if ("equals".equals(operator) && !actualValue.equals(expectedValue)) {
                        log.warn("JsonPath断言失败 [{}]: 预期 {}, 实际 {}", expression, expectedValue, actualValue);
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            log.error("断言执行异常", e);
            return false;
        }
        return true;
    }
}