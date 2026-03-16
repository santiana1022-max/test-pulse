package ink.testpulse.config;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * OkHttp3 全局配置类
 * 保证整个 Spring Boot 生命周期内只有一个 OkHttpClient 实例，复用底层的 TCP 连接池
 */
@Configuration
public class OkHttpConfig {

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                // 连接超时时间: 10秒 (建立 TCP 连接的最大等待时间)
                .connectTimeout(10, TimeUnit.SECONDS)
                // 读取超时时间: 30秒 (考虑到有些业务接口处理比较慢，这里放宽一点)
                .readTimeout(30, TimeUnit.SECONDS)
                // 写入超时时间: 10秒 (发送请求体数据到服务器的时间)
                .writeTimeout(10, TimeUnit.SECONDS)
                // 配置连接池: 最大 50 个空闲连接，空闲连接最多保持 5 分钟存活
                .connectionPool(new ConnectionPool(50, 5, TimeUnit.MINUTES))
                // 如果遇到重定向是否自动跟随 (对于测试平台，建议设为 true，或者根据未来需求动态调整)
                .followRedirects(true)
                .build();
    }
}