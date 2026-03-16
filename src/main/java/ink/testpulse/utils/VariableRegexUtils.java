package ink.testpulse.utils;

import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 全局变量正则替换工具类
 * 专门处理类似 {{global_token}} 的动态变量注入
 */
public class VariableRegexUtils {

    /**
     * 匹配 {{variableName}} 的正则表达式
     * \\{\\{ 匹配开头的双大括号
     * ([^}]+) 捕获组：匹配中间任意非右大括号的字符 (即变量名)
     * }} 匹配结尾的双大括号
     */
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{([^}]+)}}");

    /**
     * 将文本中的 {{xxx}} 替换为 variables 字典中对应的值
     *
     * @param text      原始文本 (可能是 URL、JSON Body 或 Header 字符串)
     * @param variables 当前运行环境的全局变量池
     * @return 替换后的真实文本
     */
    public static String replaceVariables(String text, Map<String, Object> variables) {
        // 如果文本为空，或者变量池为空，直接原样返回
        if (!StringUtils.hasText(text) || variables == null || variables.isEmpty()) {
            return text;
        }

        Matcher matcher = VARIABLE_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            // 提取出大括号中间的变量名，去掉首尾空格
            String varName = matcher.group(1).trim();

            // 从变量池中获取对应的值
            Object value = variables.get(varName);

            // 如果变量池中存在该变量，则将其转为字符串用于替换；如果不存在，则保持 {{xxx}} 原样输出
            String replacement = (value != null) ? String.valueOf(value) : matcher.group(0);

            // 核心避坑：Matcher.quoteReplacement 可以防止替换字符串中包含 $ 或 \ 等正则特殊字符导致报错
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }

        // 将剩余未匹配的部分追加进来
        matcher.appendTail(sb);

        return sb.toString();
    }
}