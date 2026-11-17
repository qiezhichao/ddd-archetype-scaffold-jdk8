package cn.zachqie.scaffold;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Desc 启动类
 * @Author qiezhichao
 * @Date 2022/10/21 10:43
 */
@SpringBootApplication
@Slf4j
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        log.info("***********************************************");
        log.info("Application start successful!");
        log.info("***********************************************");
    }
}
