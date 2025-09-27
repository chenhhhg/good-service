package bupt.goodservice.config;

import bupt.goodservice.model.enums.ServiceRequestStatus;
import bupt.goodservice.model.enums.ServiceResponseStatus;
import bupt.goodservice.model.enums.UserType;
import org.apache.ibatis.type.MappedTypes;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisConfig {
    @Bean
    public ConfigurationCustomizer mybatisConfigurationCustomizer() {
        return configuration -> {
            configuration.getTypeHandlerRegistry().register(UserType.class, new EnumValueTypeHandler<>(UserType.class));
            configuration.getTypeHandlerRegistry().register(ServiceRequestStatus.class, new EnumValueTypeHandler<>(ServiceRequestStatus.class));
            configuration.getTypeHandlerRegistry().register(ServiceResponseStatus.class, new EnumValueTypeHandler<>(ServiceResponseStatus.class));
        };
    }
}
