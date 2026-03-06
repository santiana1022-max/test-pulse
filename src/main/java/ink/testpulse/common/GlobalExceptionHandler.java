package ink.testpulse.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException e) {
        log.warn("业务逻辑拦截: {}", e.getMessage());
        // 注意这里：从自定义异常中直接取出 code 和 msg
        return new Result<>(e.getCode(), e.getMessage(), null);
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: ", e);
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("系统未知错误: ", e);
        return Result.error(ResultCode.ERROR); // 使用枚举中的默认错误
    }

    /**
     * 捕获 @Validated 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();

        // 提取第一条校验失败的提示信息 (例如: "所属项目ID不能为空")
        FieldError firstError = bindingResult.getFieldError();
        String errorMsg = firstError != null ? firstError.getDefaultMessage() : ResultCode.VALIDATE_FAILED.getMsg();

        // 为了避开 Result 可能没有 error(int, String) 方法的问题，我们直接实例化并赋值
        Result<String> result = new Result<>();
        result.setCode(ResultCode.VALIDATE_FAILED.getCode());
        result.setMsg(errorMsg); // 动态覆盖为具体的报错信息
        result.setData(null);

        return result;
    }
}