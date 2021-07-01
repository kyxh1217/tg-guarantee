package com.yonyou.zbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;


@ServletComponentScan(basePackages = {"com.yonyou.zbs"})
@SpringBootApplication
public class TgGuaranteeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TgGuaranteeApplication.class, args);
    }

}
