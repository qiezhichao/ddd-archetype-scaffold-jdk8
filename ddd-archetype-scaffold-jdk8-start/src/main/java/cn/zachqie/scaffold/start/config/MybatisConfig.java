package cn.zachqie.scaffold.start.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName MybatisConfig
 * @Author qiezhichao
 * @Date 2025/8/19 15:02
 */
@Configuration
@MapperScan("cn.zachqie.scaffold.infrastructure.dao.mapper")
public class MybatisConfig {

    /**
     * 注册分页插件，使 BaseMapper 与 XML 自定义分页查询能正确返回 total
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
