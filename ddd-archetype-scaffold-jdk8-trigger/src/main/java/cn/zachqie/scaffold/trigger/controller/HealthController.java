package cn.zachqie.scaffold.trigger.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName cn.zachqie.scaffold.trigger.controller.HealthController
 * @Author qiezhichao
 * @Date 2022/10/25 17:36
 */
@RestController
public class HealthController {

    @RequestMapping(value = "/health", method = RequestMethod.GET)
    public String health(){
        return "Hello HealthController!";
    }

}
