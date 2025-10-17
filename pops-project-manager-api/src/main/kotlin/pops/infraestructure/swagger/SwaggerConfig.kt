package pops.infraestructure.swagger

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.Contact
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("POPS Project Manager API")
                    .description("API para gerenciamento de projetos e skills")
                    .version("1.0.0")
                    .contact(
                        Contact()
                            .name("POPS Team")
                            .email("gyulia.piqueira@sptech.school")
                    )
            )
    }
}




