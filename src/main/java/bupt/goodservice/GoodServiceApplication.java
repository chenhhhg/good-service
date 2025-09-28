package bupt.goodservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("bupt.goodservice.mapper")
public class GoodServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoodServiceApplication.class, args);
    }

}
