package com.demo.samples.seata;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@Slf4j
@SpringBootApplication
@EnableEurekaClient
//@EnableFeignClients
@MapperScan("com.demo.samples.seata.mapper")
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
        log.info("open http://localhost:9092/doc.html");
    }
}