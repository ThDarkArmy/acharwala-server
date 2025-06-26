package tda.darkarmy.acharwala.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI acharwalaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Acharwala API")
                        .description("API for pickle customization, SHG didi management, and retailer systems")
                        .version("1.0.0")
                );
    }
}